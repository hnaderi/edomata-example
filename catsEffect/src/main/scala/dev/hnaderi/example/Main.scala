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

import cats.effect.IO
import cats.effect.IOApp
import edomata.core.CommandMessage

import java.time.Instant

object Main extends IOApp.Simple {

  private val now = IO.realTime.map(d => Instant.EPOCH.plusNanos(d.toNanos))

  def run: IO[Unit] = Application[IO]().use(app =>
    for {
      // You can use backend and service for each of the domains
      // You will most likely need to integrate them to your infrastructure
      // For example having a http web service serving the commands,
      // or listen to incoming commands from a queue and process them.
      //
      // But we will directly call a few commands as an example here
      t0 <- now
      _ <- app.accounts.service(
        CommandMessage("cmd-id-1", t0, "account-1", accounts.Command.Open)
      )

      // After a while some money arrives, so let's deposit it
      // We need a new command id, as edomata ignores redundant commands and you need to
      // provide a unique client command id, and retry as much as you need if anything bad happens
      // But each command id is gurranteed to be processed exactly once.
      t1 <- now
      _ <- app.accounts.service(
        CommandMessage(
          "cmd-id-2",
          t0,
          "account-1",
          accounts.Command.Deposit(10)
        )
      )

      // You can listen to events and notifications, or even stream the history of an entity

      _ <- app.accounts.storage.repository
        .history("account-1")
        .printlns
        .compile
        .drain
      _ <- app.accounts.storage.outbox.read.printlns.compile.drain

    } yield ()
  )

}
