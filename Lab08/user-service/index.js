const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8081;

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());

const users = [
  { id: 1, username: 'alice', password: '1234', fullName: 'Alice Nguyen', email: 'alice@example.com' },
  { id: 2, username: 'bob', password: '1234', fullName: 'Bob Tran', email: 'bob@example.com' },
  { id: 3, username: 'admin', password: 'admin', fullName: 'Admin User', email: 'admin@example.com' }
];
let nextUserId = 4;

app.get('/health', (req, res) => {
  res.json({ service: 'user-service', status: 'UP' });
});

// POST /login
app.post('/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find((item) => item.username === username && item.password === password);

  if (!user) {
    return res.status(401).json({ message: 'Invalid username or password' });
  }

  const { password: _password, ...safeUser } = user;
  return res.json({ message: 'Login successful', user: safeUser });
});

// POST /register
app.post('/register', (req, res) => {
  const { username, password, fullName } = req.body;

  if (!username || !password || !fullName) {
    return res.status(400).json({ message: 'username, password and fullName are required' });
  }

  const existing = users.find((item) => item.username === username);
  if (existing) {
    return res.status(409).json({ message: 'Username already exists' });
  }

  const newUser = {
    id: nextUserId++,
    username,
    password,
    fullName,
    email: `${username}@example.com`
  };
  users.push(newUser);

  const { password: _password, ...safeUser } = newUser;
  return res.status(201).json({ message: 'Registration successful', user: safeUser });
});

// GET /users/:id
app.get('/users/:id', (req, res) => {
  const userId = Number(req.params.id);
  const user = users.find((item) => item.id === userId);

  if (!user) {
    return res.status(404).json({ message: 'User not found' });
  }

  const { password: _password, ...safeUser } = user;
  return res.json(safeUser);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`User Service is running at http://0.0.0.0:${PORT}`);
});
