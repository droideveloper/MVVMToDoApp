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
package org.fs.mvvm.todo.viewmodels;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Locale;
import org.fs.mvvm.data.AbstractViewModel;
import org.fs.mvvm.listeners.OnSoftKeyboardAction;
import org.fs.mvvm.managers.BusManager;
import org.fs.mvvm.todo.BR;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.entities.Category;
import org.fs.mvvm.todo.events.AddEntryEventType;
import org.fs.mvvm.todo.events.StateChangeEventType;
import org.fs.mvvm.todo.managers.DatabaseManager;
import org.fs.mvvm.todo.managers.IDatabaseManager;
import org.fs.mvvm.todo.views.MainActivityViewType;
import org.fs.mvvm.todo.views.adapters.CategoryStateAdapter;
import org.fs.mvvm.utils.Objects;

public final class MainActivityViewModel extends AbstractViewModel<MainActivityViewType> {

  private CategoryStateAdapter itemSource;
  private String newTodo;

  private Disposable disposable;
  private IDatabaseManager dbManager;

  private ObservableList<Category> dataSource;
  public final OnSoftKeyboardAction imeOptionsCallback = (ime) ->  {
    if (Objects.isNullOrEmpty(newTodo)) {
      String errorEnterText = view.getStringResource(R.string.enterTextError);
      view.showError(errorEnterText);
      return true;
    }
    int maskedImeAction = ime & EditorInfo.IME_MASK_ACTION;
    if (maskedImeAction == EditorInfo.IME_ACTION_DONE) {
      AddEntryEventType addEvent = new AddEntryEventType(newTodo);
      dbManager.create(addEvent.toEntry())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(x -> {
            BusManager.send(addEvent);
            setNewTodo(null);
          }, error -> {
            if (view.isAvailable()) {
              String errorStr = view.getStringResource(R.string.addError);
              view.showError(errorStr);
              log(error);
            }
          });
      return true;
    }
    return false;
  };
  public final boolean requestTextViewFocus = false;

  public MainActivityViewModel(MainActivityViewType view) {
    super(view);
    this.dataSource = new ObservableArrayList<>();
    this.itemSource = new CategoryStateAdapter(view.getSupportFragmentManager(), dataSource);
    this.dbManager = new DatabaseManager(view.getContext());
  }

  @Override public void onStart() {
    if (Objects.isNullOrEmpty(dataSource)) {
      String titleAll = view.getStringResource(R.string.titleAll);
      dataSource.add(new Category(Category.ALL, titleAll));
      String titleActive = view.getStringResource(R.string.titleActive);
      dataSource.add(new Category(Category.ACTIVE, titleActive));
      String titleCompleted = view.getStringResource(R.string.titleCompleted);
      dataSource.add(new Category(Category.COMPLETED, titleCompleted));
    }
    disposable = BusManager.add((event) -> {
      if (event instanceof StateChangeEventType) {
        StateChangeEventType stateEvent = Objects.toObject(event);
        if (stateEvent.isActive()) {
          dbManager.update(stateEvent.toEntry())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(x -> {
                log(Log.INFO,
                    String.format(Locale.ENGLISH, "%s is activated. %s",
                        stateEvent.toEntry().getTodoName(), String.valueOf(x))
                );
              });
        } else if (stateEvent.isCompleted()) {
          dbManager.update(stateEvent.toEntry())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(x -> {
                log(Log.INFO,
                    String.format(Locale.ENGLISH, "%s is completed. %s",
                        stateEvent.toEntry().getTodoName(), String.valueOf(x))
                );
              });
        }
      }
    });
  }

  @Override public void onStop() {
    if (disposable != null) {
      BusManager.remove(disposable);
      disposable = null;
    }
  }

  @Bindable public CategoryStateAdapter getItemSource() {
    return this.itemSource;
  }

  public void setItemSource(CategoryStateAdapter itemSource) {
    this.itemSource = itemSource;
    notifyPropertyChanged(BR.itemSource);
  }

  @Bindable public String getNewTodo() {
    return this.newTodo;
  }

  public void setNewTodo(String newTodo) {
    this.newTodo = newTodo;
    notifyPropertyChanged(BR.newTodo);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return MainActivityViewModel.class.getSimpleName();
  }
}