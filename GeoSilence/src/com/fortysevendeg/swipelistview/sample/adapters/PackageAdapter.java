/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fortysevendeg.swipelistview.sample.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

import edu.cmich.cps396m.geosilence.R;
import edu.cmich.cps396m.geosilence.app.RuleList;

public class PackageAdapter extends BaseAdapter {

    private List<PackageItem> data;
    private Context context;
    private final Handler mHandler;

    public PackageAdapter(Context context, List<PackageItem> data, Handler handler) {
        this.context = context;
        this.data = data;
        this.mHandler = handler;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PackageItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PackageItem item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.package_row, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.example_row_tv_title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.example_row_tv_description);
            holder.bAction1 = (ImageButton) convertView.findViewById(R.id.button_edit_rule);
            holder.bAction2 = (Button) convertView.findViewById(R.id.button_disable_rule);
            holder.bAction3 = (ImageButton) convertView.findViewById(R.id.button_delete_rule);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SwipeListView)parent).recycle(convertView, position);

        holder.tvTitle.setText(item.getName());
        holder.tvDescription.setText(item.toString());


        holder.bAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.obtainMessage(RuleList.MESSAGE_EDIT_RULE, position, -1).sendToTarget();
            }
        });

        holder.bAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.obtainMessage(RuleList.MESSAGE_DISABLE_RULE, position, -1).sendToTarget();
            }
        });

        holder.bAction3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.obtainMessage(RuleList.MESSAGE_DELETE_RULE, position, -1).sendToTarget();
            }
        });


        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        ImageButton bAction1;
        Button bAction2;
        ImageButton bAction3;
    }

}
