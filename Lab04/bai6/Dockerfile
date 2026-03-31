# Stage 1: build
FROM node:18 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Stage 2: run
FROM node:18-alpine

WORKDIR /app

COPY --from=build /app .

EXPOSE 3000

CMD ["npm", "start"]