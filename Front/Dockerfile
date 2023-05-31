FROM node:16
WORKDIR /ovpn4all-front
COPY package.json package-lock.json ./
RUN npm ci  --save --legacy-peer-deps
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["node", "build"]
