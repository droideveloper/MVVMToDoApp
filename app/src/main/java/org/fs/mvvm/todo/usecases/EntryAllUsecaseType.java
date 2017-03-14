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
package org.fs.mvvm.todo.usecases;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import org.fs.mvvm.data.UsecaseType;
import org.fs.mvvm.listeners.Callback;
import org.fs.mvvm.managers.AbstractManager;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.managers.IDatabaseManager;
import org.fs.mvvm.utils.Preconditions;
public final class EntryAllUsecaseType extends AbstractManager implements
    UsecaseType<List<Entry>, Observable> {

  private Disposable disposable;
  private IDatabaseManager dbManager;

  EntryAllUsecaseType(IDatabaseManager dbManager) {
    Preconditions.checkNotNull(dbManager, "dbManager is null");
    this.dbManager = dbManager;
  }

  @Override public boolean isDisposed() {
    return disposable == null || disposable.isDisposed();
  }

  public Builder newBuilder() {
    dispose();//we do not want previous one call all the way around
    return new Builder()
        .dbManager(dbManager);
  }

  @Override public void dispose() {
    if (!isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  @Override public Observable<List<Entry>> sync() {
    return dbManager.all();
  }

  @Override public void async(Callback<List<Entry>> callback) {
    disposable = sync().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, callback::onCompleted);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return EntryAllUsecaseType.class.getSimpleName();
  }

  public static class Builder {
    private IDatabaseManager dbManager;
    public Builder() { }
    public Builder dbManager(IDatabaseManager dbManager) { this.dbManager = dbManager; return this; }
    public EntryAllUsecaseType build() {
      return new EntryAllUsecaseType(dbManager);
    }
  }
}