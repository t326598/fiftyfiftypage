import axios, { AxiosRequestConfig } from "axios";

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

const instance = axios.create({
    baseURL: "http://localhost:8080/"
})

function insertFile(formData: any){
    return instance.post("/files", formData);
}

function fetchFiles(params: FileParams) {
  return instance.get("/files",{params})
}
function updateFiles(formData: any) {
  return instance.post("/files/update",formData)
}

function deleteImage(no : number) {
    return instance.delete(`/files/${no}`);
  
}


export default{
  insertFile,
  fetchFiles,
  deleteImage,
  updateFiles
};