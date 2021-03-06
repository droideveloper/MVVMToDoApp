/*
 * todos Copyright (C) 2016 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.mvvm.todo.events;

import org.fs.mvvm.core.EventType;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.utils.Preconditions;

public final class AddEntryEventType implements EventType {

  private final String todoName;

  public AddEntryEventType(String todoName) {
    Preconditions.checkNotNull(todoName, "todoName is null");
    this.todoName = todoName;
  }

  public Entry toEntry() {
    return new Entry.Builder()
        .todoName(todoName)
        .todoState(Entry.ACTIVE)
        .build();
  }
}