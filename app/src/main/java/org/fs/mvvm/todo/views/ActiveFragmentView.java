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
package org.fs.mvvm.todo.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.inject.Inject;
import org.fs.mvvm.common.AbstractFragment;
import org.fs.mvvm.injections.AbstractFragmentModule;
import org.fs.mvvm.todo.BR;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.common.DaggerFragmentComponent;
import org.fs.mvvm.todo.common.FragmentModule;
import org.fs.mvvm.todo.entities.Category;
import org.fs.mvvm.todo.viewmodels.ActiveFragmentViewModel;
import org.fs.mvvm.utils.Objects;
import org.fs.mvvm.utils.Preconditions;

public final class ActiveFragmentView extends AbstractFragment<ActiveFragmentViewModel>
    implements ActiveFragmentViewType {

  @Inject ActiveFragmentViewModel viewModel;
  private ViewDataBinding viewDataBinding;

  public static ActiveFragmentView newInstance(Category category) {
    Preconditions.checkNotNull(category, "category is null");
    Bundle args = new Bundle();
    args.putParcelable(ActiveFragmentViewModel.KEY_CATEGORY, category);
    ActiveFragmentView fragment = new ActiveFragmentView();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, ViewGroup parent, Bundle restoreState) {
    viewDataBinding =
        DataBindingUtil.inflate(factory, R.layout.view_active_fragment, parent, false);
    return viewDataBinding != null ? viewDataBinding.getRoot() : null;
  }

  @Override public void onActivityCreated(Bundle restoreState) {
    super.onActivityCreated(restoreState);
    DaggerFragmentComponent.builder()
        .abstractFragmentModule(new AbstractFragmentModule(this))
        .fragmentModule(new FragmentModule())
        .build()
        .inject(this);
    viewDataBinding.setVariable(BR.viewModel, viewModel);
    viewModel.restoreState(restoreState != null ? restoreState : getArguments());
    viewModel.onCreate();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    viewModel.storeState(outState);
  }

  @Override public void onStart() {
    super.onStart();
    viewModel.onStart();
  }

  @Override public void onStop() {
    viewModel.onStop();
    super.onStop();
  }

  @Override public void showError(String errorString) {
    final View view = view();
    if (!Objects.isNullOrEmpty(view)) {
      Snackbar.make(view, errorString, Snackbar.LENGTH_LONG).show();
    }
  }

  @Override public void showError(String errorString, String actionTextString,
      View.OnClickListener callback) {
    final View view = view();
    if (!Objects.isNullOrEmpty(view)) {
      final Snackbar snackbar = Snackbar.make(view, errorString, Snackbar.LENGTH_LONG);
      snackbar.setAction(actionTextString, v -> {
        if (callback != null) {
          callback.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    }
  }

  @Override public String getStringResource(@StringRes int stringId) {
    return getActivity().getString(stringId);
  }

  @Override public Context getContext() {
    return getActivity();
  }

  @Override public boolean isAvailable() {
    return super.isAvailable();
  }

  @Override public void finish() {
    throw new IllegalArgumentException("fragment instances does not support finish options");
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return ActiveFragmentView.class.getSimpleName();
  }

  @Override protected View view() {
    return viewDataBinding != null ? viewDataBinding.getRoot() : null;
  }
}