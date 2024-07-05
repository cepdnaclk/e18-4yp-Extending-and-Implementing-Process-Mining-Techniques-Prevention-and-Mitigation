# !python -m spacy download en_core_web_md
import spacy

# Load NLP model with vectors
nlp = spacy.load('en_core_web_md')

# Keywords to use
keywords = [
    'Validate application', 'Call after offers', 'Complete application',
    'Handle leads', 'Create Offer', 'Sent (mail and online)', 'Validating',
    'Create Application', 'Accepted', 'Cancelled', 'Refused', 'Denied',
    'Assess potential fraud', 'Shortened completion', 'Personal Loan collection'
]

def calculate_similarity(question, keyword):
    question_doc = nlp(question)
    keyword_doc = nlp(keyword)
    return question_doc.similarity(keyword_doc)

def enhance_question(question, keyword):
    doc = nlp(question)
    insert_position = len(doc.text)
    for token in doc:
        if token.dep_ in ("dobj", "pobj", "nsubj", "ROOT"):
            insert_position = token.idx + len(token.text)
            break
    enhanced_question = question[:insert_position] + f" regarding {keyword}" + question[insert_position:]
    return enhanced_question

def enhance_questions_chain(questionnaire_chain, keywords):
    def enhance_sub_questions(sub_questions, keywords, used_keywords):
        enhanced_sub_questions = []
        for sub_question in sub_questions:
            if isinstance(sub_question, tuple):
                sub_question_text, sub_answer_type, *nested_sub_questions = sub_question
                best_sub_keyword = None
                best_sub_score = -1

                for keyword in keywords:
                    if keyword in used_keywords:
                        continue
                    similarity_score = calculate_similarity(sub_question_text, keyword)
                    if similarity_score > best_sub_score:
                        best_sub_score = similarity_score
                        best_sub_keyword = keyword

                if best_sub_keyword:
                    enhanced_sub_question_text = enhance_question(sub_question_text, best_sub_keyword)
                    used_keywords.add(best_sub_keyword)
                else:
                    enhanced_sub_question_text = sub_question_text

                if nested_sub_questions:
                    nested_sub_questions = nested_sub_questions[0]
                    enhanced_nested_sub_questions = enhance_sub_questions(nested_sub_questions, keywords, used_keywords)
                    enhanced_sub_questions.append((enhanced_sub_question_text, sub_answer_type, enhanced_nested_sub_questions))
                else:
                    enhanced_sub_questions.append((enhanced_sub_question_text, sub_answer_type))
        return enhanced_sub_questions

    enhanced_chain = []
    used_keywords = set()

    for main_question in questionnaire_chain:
        main_question_text, answer_type, *sub_questions = main_question
        best_keyword = None
        best_score = -1

        for keyword in keywords:
            if keyword in used_keywords:
                continue
            similarity_score = calculate_similarity(main_question_text, keyword)
            if similarity_score > best_score:
                best_score = similarity_score
                best_keyword = keyword

        if best_keyword:
            enhanced_main_question = enhance_question(main_question_text, best_keyword)
            used_keywords.add(best_keyword)
        else:
            enhanced_main_question = main_question_text

        if sub_questions:
            sub_questions = sub_questions[0]
            enhanced_sub_questions = enhance_sub_questions(sub_questions, keywords, used_keywords)
            enhanced_chain.append((enhanced_main_question, answer_type, enhanced_sub_questions))
        else:
            enhanced_chain.append((enhanced_main_question, answer_type))

    return enhanced_chain

# Chain of questions
questionnaire_chain = [
    ("Do your IT systems allow users to modify or overwrite automatically generated activity labels?", "Yes/No", [
        ("How is this functionality justified? (e.g., flexibility, user needs)", ""),
        ("Do your data entry tools have any features to validate or warn users about potential inconsistencies when modifying activity labels?", "Yes/No")
    ]),
    ("Do you have different IT systems in your process?", "Yes/No", [
        ("Do these systems use a standardized vocabulary or controlled list for activity labels?", "Yes/No"),
        ("How are inconsistencies between different systems managed during data integration? (e.g., mapping, normalization)", "")
    ]),
    ("Is any activity label information entered manually during your data collection process?", "Yes/No", [
        ("Do different process participants use slightly different terminology or abbreviations for the same activity?", "Yes/No", [
            ("Provide examples of such variations.", "")
        ]),
        ("Do your data entry tools have any features to suggest or enforce a standardized vocabulary for activity labels?", "Yes/No")
    ])
]

# Generate enhanced questions
enhanced_chain = enhance_questions_chain(questionnaire_chain, keywords)

for main_question, answer_type, sub_questions in enhanced_chain:
    print(main_question, answer_type)
    for sub_question in sub_questions:
        if len(sub_question) == 2:
            sub_question_text, sub_answer_type = sub_question
            print("  ", sub_question_text, sub_answer_type)
        elif len(sub_question) == 3:
            sub_question_text, sub_answer_type, nested_sub_questions = sub_question
            print("  ", sub_question_text, sub_answer_type)
            for nested_sub_question in nested_sub_questions:
                nested_sub_question_text, nested_sub_answer_type = nested_sub_question
                print("    ", nested_sub_question_text, nested_sub_answer_type)
