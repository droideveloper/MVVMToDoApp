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
import org.fs.mvvm.scope.ForActivity;
import org.fs.mvvm.todo.viewmodels.MainActivityViewModel;
import org.fs.mvvm.todo.views.MainActivityViewType;
import org.fs.mvvm.utils.Objects;

@Module
public class ActivityModule {

  @Provides @ForActivity public MainActivityViewModel provideMainActivityViewModel(ViewType view) {
    MainActivityViewType iview = Objects.toObject(view);
    return new MainActivityViewModel(iview);
  }
}
