MOODLE_PORT=8090

docker run -d --name DB \
  -p 3306:3306 \
  -e MYSQL_DATABASE=moodle -e MYSQL_ROOT_PASSWORD=moodle -e MYSQL_USER=moodle -e MYSQL_PASSWORD=moodle \
  mysql:5

docker run -d --name moodle \
  --link DB:DB \
  -p "${MOODLE_PORT}:80" \
  -e MOODLE_URL=http://localhost:${MOODLE_PORT} \
   jhardison/moodle
