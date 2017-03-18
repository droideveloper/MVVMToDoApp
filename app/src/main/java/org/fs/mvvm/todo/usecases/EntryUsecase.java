/*
 * UIBinding Android Copyright (C) 2017 Fatih.
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
package org.fs.mvvm.todo.usecases;

import android.util.Log;
import io.reactivex.Observable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.fs.mvvm.data.UsecaseType;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.managers.IDatabaseManager;

public final class EntryUsecase implements UsecaseType<List<Entry>> {

  private final IDatabaseManager dbManager;

  public EntryUsecase(IDatabaseManager dbManager) {
    this.dbManager = dbManager;
  }

  @Override public Observable<List<Entry>> async() {
    return dbManager.all();
  }

  protected void log(String msg) {
    log(Log.DEBUG, msg);
  }

  protected void log(Throwable exp) {
    StringWriter strWriter = new StringWriter(128);
    PrintWriter ptrWriter = new PrintWriter(strWriter);
    exp.printStackTrace(ptrWriter);
    log(Log.ERROR, strWriter.toString());
  }

  protected void log(int lv, String msg) {
    if (isLogEnabled()) {
      Log.println(lv, getClassTag(), msg);
    }
  }

  protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  protected String getClassTag() {
    return EntryUsecase.class.getSimpleName();
  }
}