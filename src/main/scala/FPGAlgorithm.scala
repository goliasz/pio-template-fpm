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

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import grizzled.slf4j.Logger
import org.apache.spark.mllib.fpm.{FPGrowth,FPGrowthModel}

case class AlgorithmParams(
  val minSupport: Double,
  val minConfidence: Double,
  val numPartitions: Int
) extends Params

class FPGModel(
  val resultList: List[(String,Array[String],Double)]
) extends Serializable {}

class FPGAlgorithm(val ap: AlgorithmParams) extends P2LAlgorithm[PreparedData, FPGModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): FPGModel = {
    println("Training FPM model.")
    val fpg = new FPGrowth().setMinSupport(ap.minSupport).setNumPartitions(ap.numPartitions)
    val model = fpg.run(data.transactions.cache)
    val res = model.generateAssociationRules(ap.minConfidence).map(x=>(x.antecedent.mkString(" "),x.consequent,x.confidence)).collect.toList

    new FPGModel(resultList=res)
  }

  def predict(model: FPGModel, query: Query): PredictedResult = {
    val qArr = query.items.toList.sorted.mkString(" ")
    val result = model.resultList.filter(x=>{x._1==qArr}).sortBy(_._3).map(x=>{new ConsequentItem(x._2,x._3)})

    PredictedResult(consequentItems=result.toArray)
  }
}
