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

# Load pretrained GPT-2 model and tokenizer
tokenizer = GPT2Tokenizer.from_pretrained("gpt2")
model = GPT2LMHeadModel.from_pretrained("gpt2")

# Prepare question-answer data
qa_data = [
    ("What is AstroSynth?", "AstroSynth is an innovative program designed to synthesize oils from celestial materials."),
    ("How does AstroSynth work?", "The AstroSynth program utilizes advanced technologies to extract and synthesize oils from meteorites."),
    # Add more Q&A pairs...
]

dataset = SimpleDataset(qa_data, tokenizer)
dataloader = DataLoader(dataset, batch_size=2, shuffle=True)

# Set up the optimizer
optimizer = torch.optim.AdamW(model.parameters(), lr=5e-5)

# Set model to training mode
model.train()

# Training loop (mock training)
for batch in dataloader:
    input_ids = batch["input_ids"]
    attention_mask = batch["attention_mask"]

    # Zero the gradients
    optimizer.zero_grad()

    # Forward pass
    outputs = model(input_ids, attention_mask=attention_mask, labels=input_ids)
    loss = outputs.loss

    # Backward pass
    loss.backward()

    # Update weights
    optimizer.step()

    print(f"Loss: {loss.item()}")  # Print loss for monitoring

# Save the trained model
model.save_pretrained("trained_gpt2_model")
tokenizer.save_pretrained("trained_gpt2_model")  # Save the tokenizer as well