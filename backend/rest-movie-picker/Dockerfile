FROM node

RUN npm install -g typescript

WORKDIR /app

COPY package.json  .

RUN npm install

COPY . .

EXPOSE 3000

CMD ["npm", "start"]