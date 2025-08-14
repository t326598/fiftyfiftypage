import axios, { AxiosRequestConfig } from "axios";

const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})

// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })

export interface FileItem {
  no: number;
  crt: number;
  path: string;
  name: string;
  size: number;
  trueDay: number;
  createdAt: string;
}

export interface FileParams {
  crt?: string;
  page?: number;
  rows?: number;



}



function insertFile(formData: any){
    return api.post("/files", formData);
}

function fetchFiles(params: FileParams) {
  return api.get("/files",{params})
}
function updateFiles(formData: any) {
  return api.post("/files/update",formData)
}

function deleteImage(no : number) {
    return api.delete(`/files/${no}`);
  
}


export default{
  insertFile,
  fetchFiles,
  deleteImage,
  updateFiles
};