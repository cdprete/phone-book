ARG PYTHON_VERSION=3.9.11-alpine3.14
FROM python:$PYTHON_VERSION
LABEL org.opencontainers.image.authors="cosimo.prete.damiano@gmail.com"
ARG HTTP_PORT=8080

WORKDIR /app
ADD phone_book/build/web/ /app/

ENV HTTP_PORT=$HTTP_PORT
EXPOSE $HTTP_PORT

RUN adduser -D app && chown -R app:app /app
USER app

ENTRYPOINT exec python3 -u -m http.server $HTTP_PORT
