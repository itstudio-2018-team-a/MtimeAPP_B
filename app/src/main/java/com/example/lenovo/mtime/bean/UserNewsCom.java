package com.example.lenovo.mtime.bean;

public class UserNewsCom {
        private String id;
        private String content;
        private String newsId;
        private String newsTitle;

        public String getcreate_time() {
            return create_time;
        }

        public void setcreate_time(String create_time) {
            this.create_time = create_time;
        }

        private String create_time;

        public String getnewsTitle() {
            return newsTitle;
        }

        public void setnewsTitle(String newsTitle) {
            this.newsTitle = newsTitle;
        }



        public String getnewsId() {
            return newsId;
        }

        public void setnewsId(String newsId) {
            this.newsId = newsId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
}
