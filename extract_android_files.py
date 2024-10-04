import os

# Function to check if the file is a relevant one (Kotlin, Java, Gradle files)
def is_relevant_file(filename):
    return filename.endswith((".kt"))

# Function to recursively collect the content of relevant files
def collect_relevant_files_content(root_dir):
    project_files = []

    # Walk through the entire directory recursively
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if is_relevant_file(file):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    file_content = f.read()
                    project_files.append(f"File: {file_path}\n\n{file_content}\n{'=' * 80}\n\n")
    
    return project_files

# Function to get the name of the root directory and create the output file name
def get_output_file_name(root_dir):
    # Get the base folder name
    folder_name = os.path.basename(os.path.abspath(root_dir))
    return f"{folder_name}_summary.txt"

# Function to write the collected content to an output file
def write_to_summary_file(output_file, project_files):
    with open(output_file, 'w', encoding='utf-8') as f:
        f.writelines(project_files)

# Main execution
if __name__ == "__main__":
    root_dir = '.'  # Current folder where the script is located
    output_file = get_output_file_name(root_dir)  # Generate the output file name based on the folder
    
    # Collect and write relevant files to the summary file
    project_files = collect_relevant_files_content(root_dir)
    write_to_summary_file(output_file, project_files)

    print(f"Summary of relevant files written to {output_file}")
