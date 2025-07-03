from pathlib import Path

def read_imdb_split(split_dir):
    split_dir = Path(split_dir)
    texts = []
    labels = []
    for label_dir in ["pos", "neg"]:
        for text_file in (split_dir/label_dir).iterdir():
            texts.append(text_file.read_text(encoding='utf-8'))
            labels.append(0 if label_dir == "neg" else 1)

    return texts, labels

train_texts, train_labels = read_imdb_split('aclImdb/train')
print(rain_texts)
# test_texts, test_labels = read_imdb_split('aclImdb/test')