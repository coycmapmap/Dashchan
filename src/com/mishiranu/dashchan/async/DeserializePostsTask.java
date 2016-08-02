/*
 * Copyright 2014-2016 Fukurou Mishiranu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mishiranu.dashchan.async;

import chan.content.model.Posts;

import com.mishiranu.dashchan.content.CacheManager;
import com.mishiranu.dashchan.content.model.PostItem;

public class DeserializePostsTask extends CancellableTask<Void, Void, Boolean>
{
	private final Callback mCallback;
	private final String mChanName;
	private final String mBoardName;
	private final String mThreadNumber;
	private final Posts mCachedPosts;
	
	private final CacheManager.SerializationHolder mHolder = new CacheManager.SerializationHolder();
	
	private Posts mPosts;
	private PostItem[] mPostItems;
	
	public interface Callback
	{
		public void onDeserializePostsComplete(boolean success, Posts posts, PostItem[] postItems);
	}
	
	public DeserializePostsTask(Callback callback, String chanName, String boardName, String threadNumber,
			Posts cachedPosts)
	{
		mCallback = callback;
		mChanName = chanName;
		mBoardName = boardName;
		mThreadNumber = threadNumber;
		mCachedPosts = cachedPosts;
	}
	
	@Override
	protected Boolean doInBackground(Void... params)
	{
		if (mCachedPosts != null) mPosts = mCachedPosts;
		else mPosts = CacheManager.getInstance().deserializePosts(mChanName, mBoardName, mThreadNumber, mHolder);
		mPostItems = ReadPostsTask.wrapPosts(mPosts, mChanName, mBoardName);
		return mPostItems != null && mPostItems.length > 0;
	}
	
	@Override
	public void onPostExecute(Boolean success)
	{
		mCallback.onDeserializePostsComplete(success, mPosts, mPostItems);
	}
	
	@Override
	public void cancel()
	{
		cancel(true);
		if (mHolder != null) mHolder.cancel();
	}
}