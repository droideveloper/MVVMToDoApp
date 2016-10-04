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
package org.fs.mvvm.todo.entities;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.fs.mvvm.utils.Objects;

public class Category extends BaseObservable implements Parcelable {

  @IntDef({
      ALL,
      ACTIVE,
      COMPLETED
  })
  @Retention(RetentionPolicy.SOURCE)
  private @interface CategoryID { }

  public final static int ALL = 0x01;
  public final static int ACTIVE = 0x02;
  public final static int COMPLETED = 0x03;

  @CategoryID int categoryId;
  String categoryName;

  private Category(Parcel input) {
    int read = input.readInt();
    categoryId = read == ALL ? ALL : read == ACTIVE ? ACTIVE : COMPLETED;
    categoryName = input.readString();
  }

  public Category(@CategoryID int categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }

  @CategoryID public int getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(categoryId);
    dest.writeString(categoryName);
  }

  @Override public String toString() {
    return !Objects.isNullOrEmpty(categoryName) ? categoryName : super.toString();
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<Category> CREATOR = new Creator<Category>() {
    @Override public Category createFromParcel(Parcel in) {
      return new Category(in);
    }

    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };
}
