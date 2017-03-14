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
import org.fs.mvvm.data.ViewType;
import org.fs.mvvm.injections.ForFragment;
import org.fs.mvvm.todo.viewmodels.ActiveFragmentViewModel;
import org.fs.mvvm.todo.viewmodels.AllFragmentViewModel;
import org.fs.mvvm.todo.viewmodels.CompletedFragmentViewModel;
import org.fs.mvvm.todo.views.ActiveFragmentViewType;
import org.fs.mvvm.todo.views.AllFragmentViewType;
import org.fs.mvvm.todo.views.CompletedFragmentViewType;
import org.fs.mvvm.utils.Objects;

@Module
public class FragmentModule {

  @Provides @ForFragment AllFragmentViewModel provideAllFragmentViewModel(ViewType view)  {
    AllFragmentViewType iview = Objects.toObject(view);
    return new AllFragmentViewModel(iview);
  }

  @Provides @ForFragment ActiveFragmentViewModel provideActiveFragmentViewModel(ViewType view) {
    ActiveFragmentViewType iview = Objects.toObject(view);
    return new ActiveFragmentViewModel(iview);
  }

  @Provides @ForFragment CompletedFragmentViewModel provideCompletedFragmentViewModel(ViewType view) {
    CompletedFragmentViewType iview = Objects.toObject(view);
    return new CompletedFragmentViewModel(iview);
  }
}
