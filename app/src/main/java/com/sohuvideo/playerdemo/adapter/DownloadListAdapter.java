package com.sohuvideo.playerdemo.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadErrorCodes;
import com.sohuvideo.api.SohuDownloadManager;
import com.sohuvideo.api.SohuDownloadObserver;
import com.sohuvideo.playerdemo.DownloadObserver;
import com.sohuvideo.playerdemo.R;

public class DownloadListAdapter extends BaseAdapter implements SohuDownloadObserver {
    private static final String TAG = "DownloadListAdapter";

    protected Context mContext;
    protected SohuDownloadManager mSohuDownloadManager;
    private Map<Long, DownloadItemHolder> displayedItems;// 界面上显示的项目

    private List<DownloadInfo> data; // 下载集合

    private Handler mHandler;

    public DownloadListAdapter(Context context) {
        mContext = context;
        mSohuDownloadManager = SohuDownloadManager.getInstance();
        displayedItems = new HashMap<Long, DownloadItemHolder>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void setData(List<DownloadInfo> data) {
        this.data = Collections.synchronizedList(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadItemHolder holder;
        if (convertView != null && convertView.getTag() instanceof DownloadItemHolder) {
            holder = (DownloadItemHolder) convertView.getTag();
        } else {
            holder = new DownloadItemHolder(mContext);
        }
        Object item = getItem(position);

        if (!(item instanceof DownloadInfo)) {
            return null;
        }
        displayedItems.put(((DownloadInfo) item).getTaskId(), holder);
        setupHolder(holder, (DownloadInfo) item);
        return holder.geHolderView();
    }

    private void setupHolder(DownloadItemHolder holder, DownloadInfo info) {
        holder.setDownloadInfo(info);
        holder.setTitleText(info.getTitle());
        holder.setSizeText(info.getDownloadedSize() + "/" + info.getTotalFileSize());
        holder.setProgress((int) (info.getDownloadProgress() * 100));
        switch (info.getDownloadState()) {
            case DownloadInfo.STATE_FOR_RUNNING:
                holder.setTaskStatus(TaskStatus.RUNNING);
                break;
            case DownloadInfo.STATE_FOR_STOPPED:
                holder.setTaskStatus(TaskStatus.STOPPED);
                break;
            case DownloadInfo.STATE_FOR_FAILED:
                holder.setTaskStatus(TaskStatus.STOPPED);
                break;
            case DownloadInfo.STATE_FOR_SUCCESSED:
                holder.setTaskStatus(TaskStatus.COMPLETED);
                break;
            case DownloadInfo.STATE_FOR_WAITTING:
                holder.setTaskStatus(TaskStatus.WAITTING);
                break;
        }

    }

    /**
     * 跟新数据集
     *
     * @param info
     */
    private boolean updateData(DownloadInfo info) {
        for (DownloadInfo downloadInfo : data) {
            if (downloadInfo.getTaskId() == info.getTaskId()) {
                DownloadInfo.copy(info, downloadInfo);
                return true;
            }
        }
        return false;
    }

    /**
     * 排序任务
     *
     * @param allTask
     */
    public List<DownloadInfo> sortTask(List<DownloadInfo> tasks) {
        Collections.sort(tasks, new Comparator<DownloadInfo>() {

            @Override
            public int compare(DownloadInfo lhs, DownloadInfo rhs) {
                if (lhs.getSid() > rhs.getSid()) {
                    return 1;
                } else if (lhs.getSid() < rhs.getSid()) {
                    return -1;
                } else if (lhs.getVid() > rhs.getVid()) {
                    return 1;
                } else if (lhs.getVid() < rhs.getVid()) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        return tasks;
    }

    // --------------------------------------DownloadManager
    // Callback--------------------------------------

    @Override
    public void onAdd(DownloadInfo info) {
        data.add(info);
        sortTask(data);
        notifyDataSetChanged();
    }

    @Override
    public void onWait(DownloadInfo info) {
        // ignore
    }

    @Override
    public boolean onStart(final DownloadInfo info) {
        updateData(info);
        final DownloadItemHolder holder = displayedItems.get(info.getTaskId());
        if (holder != null) {
            /*
             * onStart在异步被调用
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    holder.setTaskStatus(TaskStatus.RUNNING);
                }
            });
        }

        return true;
    }

    @Override
    public void onPause(DownloadInfo info) {
        updateData(info);
        final DownloadItemHolder holder = displayedItems.get(info.getTaskId());
        if (holder != null) {
            holder.setTaskStatus(TaskStatus.STOPPED);
        }
    }

    @Override
    public boolean onResume(DownloadInfo info) {
        updateData(info);
        final DownloadItemHolder holder = displayedItems.get(info.getTaskId());
        if (holder != null) {
            /*
             * onResume在异步被调用
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    holder.setTaskStatus(TaskStatus.RUNNING);
                }
            });
        }
        return true;
    }

    @Override
    public void onProgressed(DownloadInfo info) {
        updateData(info);
        DownloadItemHolder holder = displayedItems.get(info.getTaskId());
        if (holder != null) {
            holder.setProgress((int) (info.getDownloadProgress() * 100));
            holder.setSizeText(info.getDownloadedSize() + "/" + info.getTotalFileSize());
        }

    }

    @Override
    public void onCompleted(DownloadInfo info) {
        updateData(info);
        final DownloadItemHolder holder = displayedItems.get(info.getTaskId());
        if (holder != null) {
            holder.setTaskStatus(TaskStatus.COMPLETED);
            holder.getDownloadInfo().setDownloadState(DownloadInfo.STATE_FOR_SUCCESSED);
        }
    }

    @Override
    public void onRemoved(DownloadInfo info) {
        Log.d(TAG, "onRemoved:" + info.getVid() + info.getTitle());
        displayedItems.remove(info.getTaskId());
        for (DownloadInfo downloadInfo : data) {
            if (downloadInfo.getTaskId() == info.getTaskId()) {
                data.remove(downloadInfo);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onError(DownloadInfo info, int errorCode) {
        Log.d(TAG, "onError: " + errorCode);
        updateData(info);
        Toast.makeText(mContext, "error occured errorcode: " + DownloadObserver.getErrorMessage(errorCode),
                Toast.LENGTH_SHORT).show();
        if (errorCode == SohuDownloadErrorCodes.ADD_ERROR_MISS_ARGS || // 丢失参数
                errorCode == SohuDownloadErrorCodes.ADD_ERROR_APPKEY_ERROR || // APPKEY
                                                                              // 验证错误
                errorCode == SohuDownloadErrorCodes.ADD_ERROR_DO_NOT_SOPPORT_DOWNLOAD || // 请求视频不允许下载
                errorCode == SohuDownloadErrorCodes.ADD_ERROR_NETWORK_UNAVAILABLE || // 网络错误
                errorCode == SohuDownloadErrorCodes.DOWNLOADING_NETWORK_ERROR || // 网络错误
                errorCode == SohuDownloadErrorCodes.DOWNLOADING_IO_ERROR || // 下载过程中文件异常
                errorCode == SohuDownloadErrorCodes.ADD_ERROR_CAN_NOT_CREATE_DIRECTORY || // 无法创建保存目录
                errorCode == SohuDownloadErrorCodes.ADD_ERROR_REQUEST_VIDEO_ERROR) { // 请求视频详情错误
            final DownloadItemHolder holder = displayedItems.get(info.getTaskId());
            if (holder != null) {
                holder.setTaskStatus(TaskStatus.STOPPED);
            }
        }

    }

    // --------------------------------------INNER
    // CLASS--------------------------------------

    /**
     * ViewHolder
     *
     * @author gaoyihang
     */
    public class DownloadItemHolder implements OnClickListener {
        private DownloadInfo mDownloadInfo;

        private View holderView;
        private TextView titleView; // 标题
        private TextView sizeView; // 已下载/总大小
        private TextView statusView; // 下载状态
        private ProgressBar progressView;
        private Button btnView;

        /*
         * 1-显示Resume 2-显示Pause 3-隐藏
         */
        private TaskStatus mStatus = TaskStatus.WAITTING;

        public DownloadItemHolder(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holderView = inflater.inflate(R.layout.download_list_item, null);
            titleView = (TextView) holderView.findViewById(R.id.download_item_title);
            sizeView = (TextView) holderView.findViewById(R.id.download_item_size);
            progressView = (ProgressBar) holderView.findViewById(R.id.download_item_progress);
            progressView.setMax(100);// 进度条最大值100
            btnView = (Button) holderView.findViewById(R.id.btn_download_item);
            btnView.setText("Pause");
            btnView.setOnClickListener(DownloadItemHolder.this);
            statusView = (TextView) holderView.findViewById(R.id.txt_status_item);
        }

        public void setTaskStatus(TaskStatus taskStatus) {
            this.mStatus = taskStatus;
            switch (mStatus) {
                case WAITTING:
                    btnView.setVisibility(View.INVISIBLE);
                    statusView.setText("waitting...");
                    break;
                case RUNNING:
                    btnView.setVisibility(View.VISIBLE);
                    btnView.setText("Pause");
                    statusView.setText("");
                    break;
                case STOPPED:
                    btnView.setVisibility(View.VISIBLE);
                    btnView.setText("Resume");
                    statusView.setText("");
                    break;
                case COMPLETED:
                    btnView.setVisibility(View.INVISIBLE);
                    statusView.setText("  Completed");
                    break;
            }
        }

        /**
         * 设置标题文字
         *
         * @param title
         */
        public void setTitleText(String text) {
            titleView.setText(text);
        }

        /**
         * 设置右侧文字
         *
         * @param text
         */
        public void setSizeText(String text) {
            sizeView.setText(text);
        }

        /**
         * 设置显示进度
         *
         * @param progress
         */
        public void setProgress(int progress) {
            progressView.setProgress(progress);
        }

        /**
         * 通过DownloadInfo重新绘制控件
         *
         * @param downloadInfo
         */
        public void setDownloadInfo(DownloadInfo downloadInfo) {
            this.mDownloadInfo = downloadInfo;
        }

        public DownloadInfo getDownloadInfo() {
            return mDownloadInfo;
        }

        public View geHolderView() {
            return holderView;
        }

        /**
         * 开始暂停按钮
         */
        @Override
        public void onClick(View v) {
            switch (mStatus) {
                case STOPPED:
                    setTaskStatus(TaskStatus.WAITTING);
                    mSohuDownloadManager.resumeTask(mDownloadInfo.getTaskId()); // 继续任务
                    break;
                case RUNNING:
                    mSohuDownloadManager.pauseTask(mDownloadInfo.getTaskId()); // 暂停任务
                    break;
                default:
                    // ignore
            }
        }
    }

    /**
     * 下载中任务状态标示
     *
     * @author gaoyihang
     */
    static enum TaskStatus {
        /*
         * 等待中
         */
        WAITTING,

        /*
         * 下载中
         */
        RUNNING,

        /*
         * 已停止
         */
        STOPPED,

        /*
         * 已完成
         */
        COMPLETED
    }

}
