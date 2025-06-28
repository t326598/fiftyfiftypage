import axios from 'axios';

const instance = axios.create({
    baseURL: "http://localhost:8080/"
})

export interface YoutubeVideo {
  videoId: string;
  title: string;
  channelId: string;
  channelTitle: string;
  publishDate: string;
  subscriberCount: number;
  viewCount: number;
  thumbnailUrl: string;
}


function getYoutubeChart(){
    return instance.get("/api/chart/youtube/today");
}

function MusicChart(){
  return instance.get("/chart/fifty-fifty")
}

function getVideo(){
  return instance.get<YoutubeVideo[]>('/video');
}

function getFanVideo(){
  return instance.get<YoutubeVideo[]>('/video/fan');
}

export default{
  getYoutubeChart,
  MusicChart,
  getVideo,
  getFanVideo
};