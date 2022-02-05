package com.lesorin.sparknotifications.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.App;
import com.lesorin.sparknotifications.view.activities.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseAdapter implements Filterable
{
	private LayoutInflater _inflater;
	private List<? extends App> _appsList;
	private Context _context;

	public AppAdapter(Context context)
	{
		_context = context;
		_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_appsList = new ArrayList<>();
	}

	@Override
	public int getCount()
	{
		return _appsList.size();
	}

	@Override
	public String getItem(int position)
	{
		return _appsList.get(position).getName();
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;

		if(convertView == null)
		{
			convertView = _inflater.inflate(R.layout.app_layout, null);
			holder = new ViewHolder();
			holder.icon = convertView.findViewById(R.id.AppIcon);
			holder.name = convertView.findViewById(R.id.AppName);
			holder.selected = convertView.findViewById(R.id.AppEnabled);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}

		App app = _appsList.get(position);

		if(app != null)
		{
			Drawable appIcon = app.getIcon();

			if(appIcon != null)
			{
				holder.icon.setImageDrawable(appIcon);
			}

			holder.name.setText(app.getName());
			holder.selected.setOnCheckedChangeListener((buttonView, isChecked) ->
					((MainActivity)_context).appStateChanged(_appsList.get(position), isChecked));
			holder.selected.setChecked(app.getEnabled());
		}
		
		return convertView;
	}

	@Override
	public Filter getFilter()
	{
		return new Filter()
		{
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				//todo
				/*FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
				List<String> FilteredArrList = new ArrayList<String>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<String>(arrayList); // saves the original data in mOriginalValues
				}

				 *
				 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
				 *  else does the Filtering and returns FilteredArrList(Filtered)
				 *
				if (constraint == null || constraint.length() == 0) {

					// set the Original result to return
					results.count = mOriginalValues.size();
					results.values = mOriginalValues;
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						String data = mOriginalValues.get(i);
						if (data.toLowerCase().startsWith(constraint.toString())) {
							FilteredArrList.add(data);
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}

				return results;*/
				return null;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				//todo
				/*arrayList = (List<String>) results.values; // has the filtered values
				notifyDataSetChanged();  // notifies the data with new filtered values*/
			}
		};
	}

	public void setApps(List<? extends App> appsList)
	{
		_appsList = appsList;

		notifyDataSetChanged();
	}

	private class ViewHolder
	{
		ImageView icon;
		TextView name;
		SwitchCompat selected;
	}
}