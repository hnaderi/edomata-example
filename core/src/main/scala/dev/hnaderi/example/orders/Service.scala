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

package dev.hnaderi.example.orders

import edomata.core.*
import edomata.syntax.all.* // for convenient extension methods
import cats.syntax.all.* // to make life easier
import cats.data.ValidatedNec

enum Command {
  case Place(food: String, address: String)
  case MarkAsCooking(cook: String)
  case MarkAsCooked
  case MarkAsDelivering(unit: String)
  case MarkAsDelivered
  case Rate(score: Int)
}

enum Notification {
  case Received(food: String)
  case Cooking
  case Cooked
  case Delivering
  case Delivered
}

object OrderService extends Order.Service[Command, Notification] {
  import cats.Monad

  def apply[F[_]: Monad]: App[F, Unit] = App.router {
    case Command.Place(food, address) =>
      for {
        ns <- App.modifyS(_.place(food, address))
        _ <- App.publish(Notification.Received(food))
      } yield ()
    case Command.MarkAsCooking(cook: String) =>
      for {
        ns <- App.modifyS(_.markAsCooking(cook))
        _ <- App.publish(Notification.Cooking)
      } yield ()
    case _ => ??? // other command handling logic
  }
}
