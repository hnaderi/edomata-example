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

import cats.effect.IO
import cats.effect.kernel.Resource
import edomata.backend.*
import edomata.core.*
import edomata.skunk.*
import io.circe.generic.auto.*
import skunk.Session

object OrdersApp {
  given BackendCodec[Notification] = CirceCodec.jsonb
  given BackendCodec[Order] = CirceCodec.jsonb

  def backend(pool: Resource[IO, Session[IO]]) = Backend
    .builder(OrderService)
    .use(SkunkCQRSDriver("cqrs_example", pool))
    .build

  def apply(pool: Resource[IO, Session[IO]]) =
    backend(pool).map(s => new OrdersApp(s, s.compile(OrderService[IO])))
}

final case class OrdersApp(
    storage: cqrs.Backend[IO, Order, Rejection, Notification],
    service: DomainService[IO, CommandMessage[Command], Rejection]
    // TODO this should be OrderService.Handler[IO]
    // But the published artifact does not include this yet
)