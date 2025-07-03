import torch
from transformers import GPT2Tokenizer, GPT2LMHeadModel
from torch.utils.data import Dataset, DataLoader

# Define a simple dataset for training
class SimpleDataset(Dataset):
    def __init__(self, texts, tokenizer, max_length=128):
        tokenizer.pad_token = tokenizer.eos_token  # Set padding token to EOS
        self.encodings = tokenizer(texts, padding=True, truncation=True, max_length=max_length)

    def __len__(self):
        return len(self.encodings["input_ids"])

    def __getitem__(self, idx):
        return {k: torch.tensor(v[idx]) for k, v in self.encodings.items()}


#Load pretrained GPT-2 model and tokenizer
tokenizer = GPT2Tokenizer.from_pretrained("gpt2")
model = GPT2LMHeadModel.from_pretrained("gpt2")

qa_data = [
    ("What is AstroSynth?", "AstroSynth is an innovative program designed to synthesize oils from celestial materials."),
    # Add more Q&A pairs...
]

dataset = SimpleDataset(qa_data, tokenizer)
dataloader = DataLoader(dataset, batch_size=2, shuffle=True)



optimizer = torch.optim.AdamW(model.parameters(), lr=5e-5)

# Set model to training mode
# model.train()

# Training loop (mock training)
for batch in dataloader:
    input_ids = batch["input_ids"]
    attention_mask = batch["attention_mask"]

    optimizer.zero_grad()
    outputs = model(input_ids, attention_mask=attention_mask, labels=input_ids)
    loss = outputs.loss
    loss.backward()
    optimizer.step()

    print(f"Loss: {loss.item()}")  # Print loss for monitoring

# Save the trained model
# model.save_pretrained("trained_gpt2_model")
# tokenizer.save_pretrained("trained_gpt2_model")  # Save the tokenizer as well


# Load the trained model and tokenizer
model = GPT2LMHeadModel.from_pretrained("trained_gpt2_model")
tokenizer = GPT2Tokenizer.from_pretrained("trained_gpt2_model")
model.eval()


def ask_question(question):
    input_text = f"Question: {question} Answer:"
    input_ids = tokenizer.encode(input_text, return_tensors="pt")
    attention_mask = torch.ones(input_ids.shape, dtype=torch.long)

    with torch.no_grad():
        output = model.generate(
            input_ids,
            attention_mask=attention_mask,
            max_length=50,
            num_return_sequences=1,
            no_repeat_ngram_size=2,
            do_sample=True,
            top_k=50,
            top_p=0.95,
            temperature=0.7
        )

    answer = tokenizer.decode(output[0], skip_special_tokens=True)
    return answer.split("Answer:")[-1].strip()  # Return the generated answer