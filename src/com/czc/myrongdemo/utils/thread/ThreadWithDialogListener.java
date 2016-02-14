package com.czc.myrongdemo.utils.thread;

/**
 * 
 * ThreadWithDialogTask �?Interface
 * 
 * ThreadWithDialogListener TaskMain 子线程操�?ProgressDialog显示 OnTaskDismissed
 * 取消操作 ProgressDialog在dismiss时调�?OnTaskDone 主线程方�?
 * 
 * @author born
 * 
 */
public interface ThreadWithDialogListener {

	boolean TaskMain();

	boolean OnTaskDismissed();

	boolean OnTaskDone();

}
