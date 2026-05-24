const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8082;

app.use(cors({ origin: true, credentials: true }));
app.use(express.json());

const tours = [
  {
    id: 1,
    name: 'Vịnh Hạ Long',
    price: 2500000,
    location: 'Quảng Ninh',
    duration: '2 ngày 1 đêm',
    description: 'Khám phá kỳ quan thiên nhiên thế giới với hàng nghìn hòn đảo đá vôi, hang động kỳ bí và vịnh biển xanh ngọc bích.',
    image: 'https://images.unsplash.com/photo-1528127269322-539801943592?w=600&h=400&fit=crop',
    rating: 4.8,
    maxGuests: 20
  },
  {
    id: 2,
    name: 'Phố cổ Hội An',
    price: 3200000,
    location: 'Quảng Nam',
    duration: '3 ngày 2 đêm',
    description: 'Dạo bước trong phố cổ lung linh đèn lồng, thưởng thức ẩm thực đặc sắc và trải nghiệm văn hóa truyền thống.',
    image: 'https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?w=600&h=400&fit=crop',
    rating: 4.9,
    maxGuests: 15
  },
  {
    id: 3,
    name: 'Sapa - Fansipan',
    price: 4500000,
    location: 'Lào Cai',
    duration: '3 ngày 2 đêm',
    description: 'Chinh phục nóc nhà Đông Dương, ngắm ruộng bậc thang tuyệt đẹp và tìm hiểu văn hóa các dân tộc vùng cao.',
    image: 'https://images.unsplash.com/photo-1570366583862-f91883984fde?w=600&h=400&fit=crop',
    rating: 4.7,
    maxGuests: 12
  },
  {
    id: 4,
    name: 'Đà Nẵng - Bà Nà Hills',
    price: 3800000,
    location: 'Đà Nẵng',
    duration: '4 ngày 3 đêm',
    description: 'Tham quan Cầu Vàng nổi tiếng, vui chơi tại Sun World và tắm biển Mỹ Khê xinh đẹp.',
    image: 'https://images.unsplash.com/photo-1583417319070-4a69db38a482?w=600&h=400&fit=crop',
    rating: 4.6,
    maxGuests: 25
  },
  {
    id: 5,
    name: 'Phú Quốc',
    price: 5200000,
    location: 'Kiên Giang',
    duration: '4 ngày 3 đêm',
    description: 'Nghỉ dưỡng tại đảo ngọc với bãi biển cát trắng, lặn ngắm san hô và thưởng thức hải sản tươi sống.',
    image: 'https://images.unsplash.com/photo-1540541338287-41700207dee6?w=600&h=400&fit=crop',
    rating: 4.8,
    maxGuests: 18
  },
  {
    id: 6,
    name: 'Đà Lạt',
    price: 2800000,
    location: 'Lâm Đồng',
    duration: '3 ngày 2 đêm',
    description: 'Thành phố ngàn hoa với khí hậu mát mẻ, thác nước hùng vĩ và những đồi chè xanh bát ngát.',
    image: 'https://images.unsplash.com/photo-1555921015-5532091f6026?w=600&h=400&fit=crop',
    rating: 4.5,
    maxGuests: 20
  }
];

app.get('/health', (req, res) => {
  res.json({ service: 'tour-service', status: 'UP' });
});

app.get('/tours', (req, res) => {
  res.json(tours);
});

app.get('/tours/:id', (req, res) => {
  const tourId = Number(req.params.id);
  const tour = tours.find((item) => item.id === tourId);

  if (!tour) {
    return res.status(404).json({ message: 'Tour not found' });
  }

  return res.json(tour);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Tour Service is running at http://0.0.0.0:${PORT}`);
});
