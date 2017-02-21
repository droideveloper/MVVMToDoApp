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

import android.databinding.Bindable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.support.annotation.IntDef;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import org.fs.mvvm.commands.RelayCommand;
import org.fs.mvvm.common.AbstractEntity;
import org.fs.mvvm.data.IConverter;
import org.fs.mvvm.managers.BusManager;
import org.fs.mvvm.todo.BR;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.events.StateChangeEvent;
import org.fs.mvvm.utils.Objects;

@DatabaseTable(tableName = "entries")
public final class Entry extends AbstractEntity {

  public final IConverter<Integer, Drawable> colorDrawableParser = (Integer color, Locale locale) -> new ColorDrawable(color);

  public final static int ACTIVE    = 0x01;
  public final static int COMPLETED = 0x02;

  @IntDef({ACTIVE, COMPLETED})
  @Retention(RetentionPolicy.SOURCE)
  private @interface EntryState { }

  @DatabaseField(generatedId = true) private int todoId;
  @DatabaseField(canBeNull = false)  private String todoName;
  @DatabaseField @EntryState         private int todoState;

  public final IConverter<Entry, SpannableString> entryToTextConverter = (o, l) -> {
    if (o == null) {
      return null;
    }
    SpannableString str = new SpannableString(o.getTodoName());
    if (o.getTodoState() == Entry.COMPLETED) {
      str.setSpan(new StrikethroughSpan(), 0, o.getTodoName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return str;
  };

  /**
   * this way I change my state of object
   */
  public final RelayCommand changeStateCommand = new RelayCommand(() -> {
    setTodoState(todoState == ACTIVE ? COMPLETED : ACTIVE);
    BusManager.Send(new StateChangeEvent(this));
  });

  Entry() {
    /**
     * Package level accessible constructor required by ormlite
     */
  }

  private Entry(Parcel input) {
    super(input);
  }

  private Entry(String todoName, @EntryState int todoState) {
    this.todoName = todoName;
    this.todoState = todoState;
  }

  @Bindable public int getTodoId() {
    return this.todoId;
  }

  public void setTodoId(int todoId) {
    this.todoId = todoId;
    notifyPropertyChanged(BR.todoId);
  }

  @Bindable public String getTodoName() {
    return this.todoName;
  }

  public void setTodoName(String todoName) {
    this.todoName = todoName;
    notifyPropertyChanged(BR.todoName);
  }

  @EntryState
  @Bindable public int getTodoState() {
    return this.todoState;
  }

  public void setTodoState(@EntryState int todoState) {
    this.todoState = todoState;
    notifyPropertyChanged(BR.todoState);
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasTodoName = input.readInt() == 1;
    if (hasTodoName) {
      todoName = input.readString();
    }
    todoState = input.readInt() == ACTIVE ? ACTIVE : COMPLETED;
    todoId = input.readInt();
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasTodoName = !Objects.isNullOrEmpty(todoName);
    out.writeInt(hasTodoName ? 1 : 0);
    if (hasTodoName) {
      out.writeString(todoName);
    }
    out.writeInt(todoState);
    out.writeInt(todoId);
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Entry entry = (Entry) o;
    return entry.getTodoId() == getTodoId();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override protected String getClassTag() {
    return Entry.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  public Builder newBuilder() {
    return new Builder()
        .todoName(this.todoName)
        .todoState(this.todoState);
  }

  public final static Creator<Entry> CREATOR = new Creator<Entry>() {

    @Override public Entry createFromParcel(Parcel input) {
      return new Entry(input);
    }

    @Override public Entry[] newArray(int size) {
      return new Entry[size];
    }
  };

  public static class Builder {
    private String todoName;
    @EntryState private int todoState;

    public Builder() { }
    public Builder todoName(String todoName) { this.todoName = todoName; return this; }
    public Builder todoState(@EntryState int todoState) { this.todoState = todoState; return this; }
    public Entry build() {
      return new Entry(todoName, todoState);
    }
  }
}