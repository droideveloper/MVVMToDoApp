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
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.fs.mvvm.data.AbstractViewModel;
import org.fs.mvvm.data.IUsecase;
import org.fs.mvvm.listeners.Callback;
import org.fs.mvvm.managers.BusManager;
import org.fs.mvvm.todo.BR;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.common.DaggerViewModelComponent;
import org.fs.mvvm.todo.common.ViewModelModule;
import org.fs.mvvm.todo.entities.Category;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.events.DeletedEvent;
import org.fs.mvvm.todo.events.RecoveredEvent;
import org.fs.mvvm.todo.events.StateChangeEvent;
import org.fs.mvvm.todo.managers.IDatabaseManager;
import org.fs.mvvm.todo.utils.SwipeDeleteCallback;
import org.fs.mvvm.todo.views.ICompletedFragmentView;
import org.fs.mvvm.todo.views.adapters.EntryRecyclerAdapter;
import org.fs.mvvm.utils.Objects;
import org.fs.mvvm.widget.RecyclerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class CompletedFragmentViewModel extends AbstractViewModel<ICompletedFragmentView> {

  public final static String KEY_CATEGORY = "entry.category";

  private Subscription eventListener;
  private Category category;
  private ObservableList<Entry> dataSource;

  @Inject RecyclerView.LayoutManager layoutManager;
  @Inject RecyclerView.ItemAnimator  itemAnimator;
  @Inject EntryRecyclerAdapter itemSource;
  @Inject ItemTouchHelper touchHelper;

  @Inject IUsecase<List<Entry>> usecase;
  @Inject IDatabaseManager dbManager;

  public CompletedFragmentViewModel(ICompletedFragmentView view) {
    super(view);
    this.dataSource = new ObservableArrayList<>();
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_CATEGORY)) {
        category = restoreState.getParcelable(KEY_CATEGORY);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (category != null) {
      storeState.putParcelable(KEY_CATEGORY, category);
    }
  }

  @Override public void onCreate() {
    DaggerViewModelComponent.builder()
        .viewModelModule(new ViewModelModule(view.getContext(),
            category.getCategoryId(), dataSource, getSwipeListener()))
        .build()
        .inject(this);
  }

  @Override public void onStart() {
    if (view.isAvailable()) {
      eventListener = BusManager.Register((event) -> {
        //register if we have state change in entry like ACTIVE to COMPLETED or COMPLETED to ACTIVE
        if (event instanceof StateChangeEvent) {
          StateChangeEvent stateEvent = Objects.toObject(event);
          if (stateEvent.isCompleted()) {
            if (!dataSource.contains(stateEvent.toEntry())) {
              dataSource.add(stateEvent.toEntry());
            }
          } else if (stateEvent.isActive()) {
            if (dataSource.contains(stateEvent.toEntry())) {
              dataSource.remove(stateEvent.toEntry());
            }
          }
        } else if (event instanceof RecoveredEvent) {
          RecoveredEvent recoverEvent = Objects.toObject(event);
          if (!dataSource.contains(recoverEvent.toEntry()) && isCompleted(recoverEvent.toEntry())) {
            dataSource.add(recoverEvent.toEntry());
          }
        } else if (event instanceof DeletedEvent) {
          DeletedEvent deleteEvent = Objects.toObject(event);
          if (dataSource.contains(deleteEvent.toEntry())) {
            dataSource.remove(deleteEvent.toEntry());
          }
        }
      });
      //execute usecase every time
      usecase.async(new Callback<List<Entry>>() {
        @Override public void onSuccess(List<Entry> data) {
          if (!Objects.isNullOrEmpty(dataSource)) {
            dataSource.clear();
          }
          if (!Objects.isNullOrEmpty(data)) {
            dataSource.addAll(data);
          }
        }

        @Override public void onError(Throwable error) {
          if (view.isAvailable()) {
            String errorStr = view.getStringResource(R.string.addError);
            view.showError(errorStr);
            log(error);
          }
        }

        @Override public void onCompleted() {
          //no-op
        }
      });

    }
  }

  @Override public void onStop() {
    if(eventListener != null) {
      BusManager.Unregister(eventListener);
      eventListener = null;
    }
  }

  private SwipeDeleteCallback.OnSwipedListener getSwipeListener() {
    return (viewHolder, swipeDirection) -> {
      if (view.isAvailable()) {
        //delete item
        final int position = viewHolder.getAdapterPosition();
        Entry deleted = dataSource.remove(position);
        dbManager.delete(deleted)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(x -> {
              //change its id.
              deleted.setTodoId(Integer.MIN_VALUE);
              log(Log.INFO,
                  String.format(Locale.ENGLISH, "%s is activated. %s",
                      deleted.getTodoName(), String.valueOf(x))
              );
            });
        BusManager.Send(new DeletedEvent(deleted));
        //create message to notify user
        String ok = view.getStringResource(android.R.string.ok);
        String msg = view.getStringResource(R.string.recoverDeleteItem);
        msg = String.format(Locale.getDefault(), msg, ok, deleted.getTodoName());
        //add deleted back if user recover click
        view.showError(msg, ok, v -> {
          if (Objects.isNullOrEmpty(dataSource)) {
            //the idea is we do not want error if collection is empty
            dataSource.add(deleted);
          } else {
            dataSource.add(position, deleted);
          }
          dbManager.create(deleted)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(x -> {
                BusManager.Send(new RecoveredEvent(deleted));
                log(Log.ERROR,
                    String.format(Locale.ENGLISH, "%s previously deleted inserted.",
                        deleted.getTodoName())
                );
              });
        });
      }
    };
  }

  @Bindable public EntryRecyclerAdapter getItemSource() {
    return this.itemSource;
  }

  public void setItemSource(EntryRecyclerAdapter itemSource) {
    this.itemSource = itemSource;
    notifyPropertyChanged(BR.itemSource);
  }

  @Bindable public RecyclerView.ItemAnimator getItemAnimator() {
    return this.itemAnimator;
  }

  public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
    this.itemAnimator = itemAnimator;
    notifyPropertyChanged(BR.itemAnimator);
  }

  @Bindable public ItemTouchHelper getTouchHelper() {
    return this.touchHelper;
  }

  public void setTouchHelper(ItemTouchHelper touchHelper) {
    this.touchHelper = touchHelper;
    notifyPropertyChanged(BR.touchHelper);
  }

  @Bindable public RecyclerView.LayoutManager getLayoutManager() {
    return this.layoutManager;
  }

  public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    notifyPropertyChanged(BR.layoutManager);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return CompletedFragmentViewModel.class.getSimpleName();
  }

  private boolean isCompleted(Entry entry) {
    return entry.getTodoState() == Entry.COMPLETED;
  }
}