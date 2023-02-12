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

package dev.hnaderi.example

import cats.effect.kernel.Async
import cats.effect.std.Console
import dev.hnaderi.example.accounts.AccountsApp
import dev.hnaderi.example.orders.OrdersApp
import natchez.Trace.Implicits.noop
import skunk.Session

final case class Application[F[_]](
    accounts: AccountsApp[F],
    orders: OrdersApp[F]
)

object Application {
  def apply[F[_]: Async: Console]() = for {
    pool <- Session.pooled[F](
      host = "localhost",
      user = "postgres",
      password = Some("postgres"),
      database = "postgres",
      max = 10
    )

    accounts <- AccountsApp(pool)
    orders <- OrdersApp(pool)
  } yield new Application(accounts, orders)
}
