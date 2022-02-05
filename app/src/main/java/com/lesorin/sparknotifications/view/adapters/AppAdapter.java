package com.lesorin.sparknotifications.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.App;
import com.lesorin.sparknotifications.view.activities.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseAdapter implements Filterable
{
	private LayoutInflater _inflater;
	private List<? extends App> _originalAppsList, _appsList;
	private Context _context;

	public AppAdapter(Context context)
	{
		_context = context;
		_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_originalAppsList = new ArrayList<>();
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
			holder.appName = convertView.findViewById(R.id.AppName);
			holder.packageName = convertView.findViewById(R.id.PackageName);
			holder.appEnabled = convertView.findViewById(R.id.AppEnabled);
			holder.clickableArea = convertView.findViewById(R.id.ClickableArea);

			holder.icon.setOnClickListener(view -> holder.selected.setChecked(!holder.selected.isChecked()));
			holder.clickableArea.setOnClickListener(view -> holder.selected.setChecked(!holder.selected.isChecked()));
			holder.selected.setOnCheckedChangeListener((buttonView, isChecked) ->
			{
				try
				{
					((MainActivity)_context).appStateChanged(_appsList.get(position), isChecked);
				}
				catch(Exception ignored)
				{
				}
			});
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

			holder.appName.setText(app.getName());
			holder.packageName.setText(app.getPackageName());
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
				FilterResults results = new FilterResults(); // Holds the results of a filtering operation in values

				if(constraint == null || constraint.length() == 0)
				{
					results.values = _originalAppsList;
					results.count = _originalAppsList.size();
				}
				else
				{
					constraint = constraint.toString().toLowerCase();

					List<App> filteredValues = new ArrayList<>();

					for(App app : _originalAppsList)
					{
						String appName = app.getName().toLowerCase();
						String packageName = app.getPackageName().toLowerCase();

						if(appName.contains(constraint) || packageName.contains(constraint))
						{
							filteredValues.add(app);
						}
					}

					results.values = filteredValues;
					results.count = filteredValues.size();
				}

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				_appsList = (List<App>)results.values;

				notifyDataSetChanged();
			}
		};
	}

	public void setApps(List<? extends App> appsList)
	{
		_originalAppsList = appsList;
		_appsList = _originalAppsList;

		notifyDataSetChanged();
	}

	private class ViewHolder
	{
		ImageView icon;
		TextView appName, packageName;
		CheckBox appEnabled;
		View clickableArea;
	}
}