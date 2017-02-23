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

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import org.fs.mvvm.data.IUsecase;
import org.fs.mvvm.listeners.Callback;
import org.fs.mvvm.managers.AbstractManager;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.managers.IDatabaseManager;
import org.fs.mvvm.utils.Preconditions;

public final class EntryActiveUsecase extends AbstractManager implements IUsecase<List<Entry>, Single> {

  private Disposable disposable;
  private IDatabaseManager dbManager;

  EntryActiveUsecase(IDatabaseManager dbManager) {
    Preconditions.checkNotNull(dbManager, "dbManager is null");
    this.dbManager = dbManager;
  }

  @Override public boolean isDisposed() {
    return disposable == null || disposable.isDisposed();
  }

  @Override public void dispose() {
    if (!isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  public Builder newBuilder() {
    dispose();
    return new Builder()
        .dbManager(dbManager);
  }

  @Override public Single<List<Entry>> sync() {
    return dbManager.all(x -> x.getTodoState() == Entry.ACTIVE);
  }

  @Override public void async(Callback<List<Entry>> callback) {
    disposable = sync().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(callback::onSuccess, callback::onError);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return EntryActiveUsecase.class.getSimpleName();
  }

  public static class Builder {
    private IDatabaseManager dbManager;
    public Builder() { }
    public Builder dbManager(IDatabaseManager dbManager) { this.dbManager = dbManager; return this; }
    public EntryActiveUsecase build() {
      return new EntryActiveUsecase(dbManager);
    }
  }
}