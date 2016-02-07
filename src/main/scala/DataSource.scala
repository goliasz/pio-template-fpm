package org.template.fpm

/*
 * Copyright KOLIBERO under one or more contributor license agreements.  
 * KOLIBERO licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params
import io.prediction.data.store.PEventStore
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

import grizzled.slf4j.Logger

case class DataSourceParams(appName: String) extends Params

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData, EmptyEvaluationInfo, Query, EmptyActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): TrainingData = {
    println("Gathering data from event server.")
    val transactionsRDD: RDD[Array[String]] = PEventStore.find(
      appName = dsp.appName,
      entityType = Some("transaction"),
      startTime = None,
      eventNames = Some(List("$set")))(sc).map { event =>
        try {
	  event.properties.get[Array[String]]("items")
        } catch {
          case e: Exception => {
            logger.error(s"Failed to convert event ${event} of. Exception: ${e}.")
            throw e
          }
        }
      }
    new TrainingData(transactionsRDD)
  }
}

class TrainingData(
  val transactions: RDD[Array[String]]
) extends Serializable
