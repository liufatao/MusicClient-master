package me.wcy.music.storage.db;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import me.wcy.music.application.AppCache;
import me.wcy.music.model.LatestMusic;
import me.wcy.music.model.LoveMusic;
import me.wcy.music.model.Music;
import me.wcy.music.storage.db.greendao.LatestMusicDao;
import me.wcy.music.storage.db.greendao.LoveMusicDao;
import me.wcy.music.storage.db.greendao.MusicDao;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.ChineseToEnglish;
import me.wcy.music.utils.ParseUtils;

/**
 * 实现数据库的增删改查操作
 */
public class MusicDaoUtils {
    private static final String TAG = "MusicDaoUtils";
    private DBManager mManager;
    public static MusicDaoUtils musicDaoUtils;
    public static MusicDaoUtils getInstance(){
        if (musicDaoUtils==null){
            musicDaoUtils=new MusicDaoUtils(AppCache.get().getContext());
        }
        return musicDaoUtils;
    }

    private MusicDaoUtils(Context context) {
        mManager = DBManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成music记录的插入，如果表未创建，先创建music表
     *
     * @param music
     * @return
     */
    public boolean insertMusic(Music music) {
        //添加索引
        if (music.getFirstLetter() == null) {
            music.setFirstLetter(ChineseToEnglish.StringToPinyinSpecial(music.getTitle()).toUpperCase());
        }
        boolean flag = false;
        flag = mManager.getDaoSession().getMusicDao().insert(music) == -1 ? false : true;
        Log.i(TAG, "insert music :" + flag + "-->" + music.toString());
        return flag;
    }

    /**
     * 插入最近播放列表
     *
     * @param latestMusic
     * @return
     */
    public boolean insertLatestMusic(LatestMusic latestMusic) {

        if (latestMusic.getFirstLetter() == null) {
            latestMusic.setFirstLetter(ChineseToEnglish.StringToPinyinSpecial(latestMusic.getTitle()).toUpperCase());
        }
        if (querySongID(latestMusic.getSongId()) == null) {
            return mManager.getDaoSession().getLatestMusicDao().insert(latestMusic) == -1 ? false : true;
        }else {
            deleteLatestMusic(querySongID(latestMusic.getSongId()));
            return mManager.getDaoSession().getLatestMusicDao().insert(latestMusic) == -1 ? false : true;
        }


    }

    /**
     * 插入我的喜爱播放列表
     * @param loveMusic
     * @return
     */
    public boolean insertLoveMusic(LoveMusic loveMusic){
      if (queryLoveSongID(loveMusic.getSongId())==null){
          return mManager.getDaoSession().getLoveMusicDao().insert(loveMusic)== -1 ? false:true;
      }else{
          return deleteLoveMusic(queryLoveSongID(loveMusic.getSongId()));
      }

    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param musicList
     * @return
     */
    public boolean insertMusicList(final List<Music> musicList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Music meizi : musicList) {
                        mManager.getDaoSession().insertOrReplace(meizi);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param musicID
     * @return
     */
    public boolean deleteMusic(int musicID) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(musicID);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param music
     * @return
     */
    public boolean deleteMusic(Music music) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().getMusicDao().delete(music);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 删除单条记录
     *
     * @param loveMusic
     * @return
     */
    public boolean deleteLoveMusic(LoveMusic loveMusic) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().getLoveMusicDao().delete(loveMusic);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param latestMusic
     * @return
     */
    public boolean deleteLatestMusic(LatestMusic latestMusic) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().getLatestMusicDao().delete(latestMusic);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(Music.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Music> queryMeiziByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(Music.class, sql, conditions);
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<Music> queryAllMusic() {
        return mManager.getDaoSession().loadAll(Music.class);
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<LoveMusic> queryAllLoveMusic() {
        return mManager.getDaoSession().loadAll(LoveMusic.class);
    }

    /**
     * 根据拼音索引排序查询所有
     *
     * @return
     */
    public List<Music> queryOrderAscMusic() {
        return mManager.getDaoSession().queryBuilder(Music.class).orderAsc(MusicDao.Properties.FirstLetter).list();
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<LatestMusic> queryAllLatestMusic() {
        return mManager.getDaoSession().queryBuilder(LatestMusic.class).orderDesc(LatestMusicDao.Properties.Date).list();
    }

    /**
     * 查询songID是否存在
     *
     * @param songID
     * @return
     */
    public LatestMusic querySongID(Long songID) {
        return mManager.getDaoSession().queryBuilder(LatestMusic.class).where(LatestMusicDao.Properties.SongId.eq(songID)).unique();
    }

    /**
     * 查询songID是否存在
     *
     * @param songID
     * @return
     */
    public LoveMusic queryLoveSongID(Long songID) {
        return mManager.getDaoSession().queryBuilder(LoveMusic.class).where(LoveMusicDao.Properties.SongId.eq(songID)).unique();
    }

    /**
     * 根据歌手名称查找音乐
     *
     * @param artist
     * @return
     */
    public List<Music> getMusicListByArtist(String artist) {
        return mManager.getDaoSession().queryBuilder(Music.class).where(MusicDao.Properties.Artist.eq(artist)).orderAsc(MusicDao.Properties.FirstLetter).list();
    }

    /**
     * 根据专辑名称查找音乐
     *
     * @param album
     * @return
     */
    public List<Music> getMusicListByAlbum(String album) {
        return mManager.getDaoSession().queryBuilder(Music.class).where(MusicDao.Properties.Album.eq(album)).orderAsc(MusicDao.Properties.FirstLetter).list();
    }

    /**
     * 根据父路径查找
     *
     * @param folder
     * @return
     */
    public List<Music> getMusicListByFolder(String folder) {
        return mManager.getDaoSession().queryBuilder(Music.class).where(MusicDao.Properties.ParentPath.eq(folder)).orderAsc(MusicDao.Properties.FirstLetter).list();
    }

    /**
     * 查询所有记录
     *
     * @return
     */

    public List<Music> queryFilterTimeMusic() {
        Long filterTime = ParseUtils.parseLong(Preferences.getFilterTime()) * 1000;
        Long filterSize = ParseUtils.parseLong(Preferences.getFilterSize()) * 1024;
        return mManager.getDaoSession().queryBuilder(Music.class).where(MusicDao.Properties.Duration.ge(filterTime)).list();
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public Music queryMusicById(long key) {
        return mManager.getDaoSession().load(Music.class, key);
    }

    /**
     * 获取歌曲路径
     *
     * @param id
     * @return
     */
    public String getMusicPath(long id) {
        return mManager.getDaoSession().queryBuilder(Music.class).where(MusicDao.Properties.SongId.eq(id)).unique().getPath();
    }

}
