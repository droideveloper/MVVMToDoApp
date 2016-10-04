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

import java.util.List;
import org.fs.mvvm.data.IUsecase;
import org.fs.mvvm.listeners.Callback;
import org.fs.mvvm.managers.AbstractManager;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.managers.IDatabaseManager;
import org.fs.mvvm.utils.Preconditions;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class EntryAllUsecase extends AbstractManager implements IUsecase<List<Entry>> {

  private Subscription taskSubscription;
  private IDatabaseManager dbManager;

  private EntryAllUsecase(IDatabaseManager dbManager) {
    Preconditions.checkNotNull(dbManager, "dbManager is null");
    this.dbManager = dbManager;
  }

  @Override public boolean isUnsubscribed() {
    return taskSubscription == null || taskSubscription.isUnsubscribed();
  }

  public Builder newBuilder() {
    unsubscribe();//we do not want previous one call all the way around
    return new Builder()
        .dbManager(dbManager);
  }

  @Override public void unsubscribe() {
    if (!isUnsubscribed()) {
      taskSubscription.unsubscribe();
      taskSubscription = null;
    }
  }

  @Override public Observable<List<Entry>> sync() {
    return dbManager.all();
  }

  @Override public void async(Callback<List<Entry>> callback) {
    taskSubscription = sync().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError, callback::onCompleted);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return EntryAllUsecase.class.getSimpleName();
  }

  public static class Builder {
    private IDatabaseManager dbManager;
    public Builder() { }
    public Builder dbManager(IDatabaseManager dbManager) { this.dbManager = dbManager; return this; }
    public EntryAllUsecase build() {
      return new EntryAllUsecase(dbManager);
    }
  }
}