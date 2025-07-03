import torch
from transformers import GPT2Tokenizer, GPT2LMHeadModel

# Load the trained model and tokenizer
model = GPT2LMHeadModel.from_pretrained("trained_gpt2_model")
tokenizer = GPT2Tokenizer.from_pretrained("trained_gpt2_model")
model.eval()

# Function to ask a question and get an answer
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

# Example questions
# print(ask_question("What is the capital of France?"))
# print(ask_question("How does AstroSynth use artificial intelligence?"))
print(ask_question("What is AstroSynth?"))