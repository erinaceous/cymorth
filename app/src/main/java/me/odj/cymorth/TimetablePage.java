package me.odj.cymorth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by owain on 9/19/13.
 */

public class TimetablePage extends Fragment {
    public Date tab_day;
    public Cursor cur;

    public TimetablePage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout
                .fragment_timetable, container, false);

        ListView lv = (ListView)rootView.findViewById(R.id.listView);
        TextView tv = (TextView)rootView.findViewById(R.id.dayLabel);
        String[] items = { "Error loading timetable" };
        Calendar c = UniCalendar.getInstance();
        c.setTime(this.tab_day);
        //tv.setText(String.format("%1$tA %1$te %1$tB", c));
        tv.setText(Utils.getLocalDate(rootView.getContext(), c));

        DBHelper helper = new DBHelper(rootView.getContext());
        cur = helper.getDaysLectures(UniCalendar.getStartOfUniDay(c.getTime()).getTime());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(rootView
                .getContext(), R.layout.fragment_list_item, cur,
                new String[] { DBHelper.colType, DBHelper.colModule,
                        DBHelper.colRoom, DBHelper.colReadableTime },
                new int[] { R.id.lectureType,
                        R.id.lectureModule, R.id.lectureRoom,
                        R.id.lectureTime }, CursorAdapter.FLAG_AUTO_REQUERY);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

        return rootView;
    }

    public void refresh() {
        cur.requery();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.lecture_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView
                .AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId() == R.id.action_export) {
            DBHelper helper = new DBHelper(getActivity());
            Slot slot = Slot.fromSQLiteCursor(helper.getLecture(info.id));
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", slot.time.getTime());
            intent.putExtra("endTime", slot.time.getTime() + (UniCalendar
                    .SLOT_LENGTH_MIN * 60000));
            intent.putExtra("allDay", false);
            intent.putExtra("rrule", "FREQ=WEEKLY");
            intent.putExtra("title", slot.module);
            intent.putExtra("description", slot.type);
            intent.putExtra("eventLocation", slot.room);
            getActivity().startActivity(intent);
            return true;
        } else if(item.getItemId() == R.id.action_clipboard) {
            DBHelper helper = new DBHelper(getActivity());
            Slot slot = Slot.fromSQLiteCursor(helper.getLecture(info.id));
            ClipboardManager clipboard = (ClipboardManager) getActivity()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            String lecture_info = String.format((String)getActivity()
                    .getString(R
                    .string.lecture_clipboard_info), slot.module,
                    slot.readableTime, slot.room);
            ClipData clip = ClipData.newPlainText("lecture information",
                    lecture_info);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(),
                    R.string.copied_to_clipboard,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }
}