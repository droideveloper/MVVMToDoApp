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
package org.fs.mvvm.todo.common;

import dagger.Module;
import dagger.Provides;
import org.fs.mvvm.data.IView;
import org.fs.mvvm.injections.ForFragment;
import org.fs.mvvm.todo.viewmodels.ActiveFragmentViewModel;
import org.fs.mvvm.todo.viewmodels.AllFragmentViewModel;
import org.fs.mvvm.todo.viewmodels.CompletedFragmentViewModel;
import org.fs.mvvm.todo.views.IActiveFragmentView;
import org.fs.mvvm.todo.views.IAllFragmentView;
import org.fs.mvvm.todo.views.ICompletedFragmentView;
import org.fs.mvvm.utils.Objects;

@Module
public class FragmentModule {

  @Provides @ForFragment AllFragmentViewModel provideAllFragmentViewModel(IView view)  {
    IAllFragmentView iview = Objects.toObject(view);
    return new AllFragmentViewModel(iview);
  }

  @Provides @ForFragment ActiveFragmentViewModel provideActiveFragmentViewModel(IView view) {
    IActiveFragmentView iview = Objects.toObject(view);
    return new ActiveFragmentViewModel(iview);
  }

  @Provides @ForFragment CompletedFragmentViewModel provideCompletedFragmentViewModel(IView view) {
    ICompletedFragmentView iview = Objects.toObject(view);
    return new CompletedFragmentViewModel(iview);
  }
}
