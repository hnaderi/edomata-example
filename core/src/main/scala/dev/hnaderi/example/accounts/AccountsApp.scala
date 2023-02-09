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

package dev.hnaderi.example.accounts

import cats.effect.IO
import cats.effect.kernel.Resource
import edomata.backend.Backend
import edomata.skunk.*
import io.circe.generic.auto.*
import skunk.Session

object AccountsApp {
  given BackendCodec[Event] = CirceCodec.jsonb // or .json
  given BackendCodec[Notification] = CirceCodec.jsonb
  given BackendCodec[Account] = CirceCodec.jsonb

  def backend(pool: Resource[IO, Session[IO]]) = Backend
    .builder(AccountService)
    .use(SkunkDriver("eventsourcing_example", pool))
    .persistedSnapshot(maxInMem = 100)
    .build

  def apply(pool: Resource[IO, Session[IO]]) =
    backend(pool).map(s => new AccountsApp(s, s.compile(AccountService[IO])))
}

final case class AccountsApp(
    storage: Backend[IO, Account, Event, Rejection, Notification],
    service: AccountService.Handler[IO]
)
