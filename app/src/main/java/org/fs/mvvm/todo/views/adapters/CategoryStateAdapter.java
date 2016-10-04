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

import android.databinding.ObservableList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.fs.mvvm.common.AbstractPagerStateBindingAdapter;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.entities.Category;
import org.fs.mvvm.todo.views.ActiveFragmentView;
import org.fs.mvvm.todo.views.AllFragmentView;
import org.fs.mvvm.todo.views.CompletedFragmentView;

public final class CategoryStateAdapter extends AbstractPagerStateBindingAdapter<Category> {

  public CategoryStateAdapter(FragmentManager fragmentManager,
      ObservableList<Category> itemSource) {
    super(fragmentManager, itemSource);
  }

  @Override protected Fragment onBindView(Category item, int viewType) {
    switch (item.getCategoryId()) {
      case Category.ALL: {
        return AllFragmentView.newInstance(item);
      }
      case Category.ACTIVE: {
        return ActiveFragmentView.newInstance(item);
      }
      case Category.COMPLETED: {
        return CompletedFragmentView.newInstance(item);
      }
      default: {
        throw new IllegalArgumentException("you somehow changed category type, don't dod that dude");
      }
    }
  }

  @Override protected int getItemViewType(int position) {
    return 0;
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return CategoryStateAdapter.class.getSimpleName();
  }
}