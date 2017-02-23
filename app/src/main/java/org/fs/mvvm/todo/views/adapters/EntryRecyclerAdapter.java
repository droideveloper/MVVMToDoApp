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
package org.fs.mvvm.todo.views.adapters;

import android.content.Context;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import org.fs.mvvm.common.AbstractRecyclerBindingAdapter;
import org.fs.mvvm.managers.BusManager;
import org.fs.mvvm.todo.BR;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.entities.Entry;
import org.fs.mvvm.todo.views.viewholders.EntryRecyclerViewHolder;

public final class EntryRecyclerAdapter
    extends AbstractRecyclerBindingAdapter<Entry, EntryRecyclerViewHolder> {

  public static EntryRecyclerAdapter create(Context context,
      ObservableList<Entry> itemSource) {
    return new EntryRecyclerAdapter(context, itemSource, SINGLE_SELECTION_MODE);
  }

  private EntryRecyclerAdapter(Context context, ObservableList<Entry> itemSource,
      int selectionMode) {
    super(context, itemSource, selectionMode);
  }

  @Override protected void bindDataViewHolder(Entry item, EntryRecyclerViewHolder viewHolder) {
    viewHolder.setItem(BR.item, item);
  }

  @Override protected EntryRecyclerViewHolder createDataViewHolder(ViewDataBinding viewDataBinding,
      BusManager busManager, int viewType) {
    return new EntryRecyclerViewHolder(viewDataBinding, busManager);
  }

  @LayoutRes @Override protected int layoutResource(int viewType) {
    return R.layout.view_todo_item;
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return EntryRecyclerAdapter.class.getSimpleName();
  }
}