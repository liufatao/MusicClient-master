package me.wcy.music.model;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * 单曲信息
 * @author Administrator
 */
@Entity
public class LoveMusic extends Music{
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @Property(nameInDb = "type")
    private int type; // 歌曲类型:本地/网络
    @Property(nameInDb = "songId")
    private long songId; // [本地]歌曲ID
    @Property(nameInDb = "title")
    private String title; // 音乐标题
    @Property(nameInDb = "artist")
    private String artist; // 艺术家
    @Property(nameInDb = "album")
    private String album; // 专辑
    @Property(nameInDb = "albumId")
    private long albumId; // [本地]专辑ID
    @Property(nameInDb = "coverPath")
    private String coverPath; // [在线]专辑封面路径
    @NotNull
    @Property(nameInDb = "duration")
    private long duration; // 持续时间
    @NotNull
    @Property(nameInDb = "path")
    private String path; // 播放地址
    @Property(nameInDb = "parentPath")
    private String parentPath; //父目录路径
    @Property(nameInDb = "fileName")
    private String fileName; // [本地]文件名
    @Property(nameInDb = "fileSize")
    private long fileSize; // [本地]文件大小
    @Property(nameInDb = "firstLetter")
    private String firstLetter;//拼音首字母

    @Generated(hash = 812727792)
    public LoveMusic() {
    }

    @Generated(hash = 1457617214)
    public LoveMusic(Long id, int type, long songId, String title, String artist,
            String album, long albumId, String coverPath, long duration, @NotNull String path,
            String parentPath, String fileName, long fileSize, String firstLetter) {
        this.id = id;
        this.type = type;
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.coverPath = coverPath;
        this.duration = duration;
        this.path = path;
        this.parentPath = parentPath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.firstLetter = firstLetter;
    }
    @Override
    public Long getId() {
        return this.id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public int getType() {
        return this.type;
    }
    @Override
    public void setType(int type) {
        this.type = type;
    }
    @Override
    public long getSongId() {
        return this.songId;
    }
    @Override
    public void setSongId(long songId) {
        this.songId = songId;
    }
    @Override
    public String getTitle() {
        return this.title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String getArtist() {
        return this.artist;
    }
    @Override
    public void setArtist(String artist) {
        this.artist = artist;
    }
    @Override
    public String getAlbum() {
        return this.album;
    }
    @Override
    public void setAlbum(String album) {
        this.album = album;
    }
    @Override
    public long getAlbumId() {
        return this.albumId;
    }
    @Override
    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
    @Override
    public String getCoverPath() {
        return this.coverPath;
    }
    @Override
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
    @Override
    public long getDuration() {
        return this.duration;
    }
    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }
    @Override
    public String getPath() {
        return this.path;
    }
    @Override
    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public String getParentPath() {
        return this.parentPath;
    }
    @Override
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
    @Override
    public String getFileName() {
        return this.fileName;
    }
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    @Override
    public long getFileSize() {
        return this.fileSize;
    }
    @Override
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    @Override
    public String getFirstLetter() {
        return this.firstLetter;
    }
    @Override
    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public interface Type {
        int LOCAL = 0;
        int ONLINE = 1;
    }

}
