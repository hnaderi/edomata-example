/*
 * Copyright 2023 Hossein Naderi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hnaderi.example.cqrs

import edomata.core.*
import edomata.syntax.all.* // for convenient extension methods
import cats.syntax.all.* // to make life easier
import cats.data.ValidatedNec

enum OrderStatus {
  case New
  case Cooking(cook: String)
  case WaitingToPickUp
  case Delivering(unit: String)
}

enum Rejection {
  case ExistingOrder
  case NoSuchOrder
  case InvalidRequest // this should be more fine grained in real world applications
}

enum Order {
  case Empty
  case Placed(
      food: String,
      address: String,
      status: OrderStatus = OrderStatus.New
  )
  case Delivered(rating: Int)

  def place(food: String, address: String) = this match {
    case Empty => Placed(food, address).asRight
    case _     => Rejection.ExistingOrder.leftNec
  }

  def markAsCooking(cook: String) = this match {
    case st @ Placed(_, _, OrderStatus.New) =>
      st.copy(status = OrderStatus.Cooking(cook)).asRight
    case _ => Rejection.InvalidRequest.leftNec
  }

  // other logics from business
}

object Order extends CQRSModel[Order, Rejection] {
  def initial = Empty
}
