import axios from 'axios';
const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})
// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })

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
    return api.get("/api/chart/youtube/today");
}

function MusicChart(){
  return api.get("/chart/fifty-fifty")
}

function getVideo(){
  return api.get<YoutubeVideo[]>('/video');
}

function getFanVideo(){
  return api.get<YoutubeVideo[]>('/video/fan');
}

export default{
  getYoutubeChart,
  MusicChart,
  getVideo,
  getFanVideo
};