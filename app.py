from flask import Flask, request, jsonify, render_template
import os

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/analyze', methods=['POST'])
def analyze_photo():
    if 'photo' not in request.files:
        return jsonify({'error': 'No photo uploaded'}), 400
    
    photo = request.files['photo']
    if photo.filename == '':
        return jsonify({'error': 'No photo selected'}), 400
    
    # 保存照片
    upload_folder = 'uploads'
    if not os.path.exists(upload_folder):
        os.makedirs(upload_folder)
    
    photo_path = os.path.join(upload_folder, photo.filename)
    photo.save(photo_path)
    
    # 这里可以添加照片分析逻辑
    # 暂时返回模拟数据
    result = {
        'score': 85,
        'composition': '良好',
        'lighting': '优秀',
        'focus': '清晰',
        'suggestions': ['尝试使用三分法则', '增加景深']
    }
    
    return jsonify(result)

if __name__ == '__main__':
    app.run(debug=True)