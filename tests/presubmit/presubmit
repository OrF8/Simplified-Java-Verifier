#!/bin/python3

import sys
import os.path

EX_ROOT_PATH = "/cs/course/current/oop1/ex/"
SOURCE_DIR_NAME = "src"

sys.path.append(EX_ROOT_PATH)
from tester import runAll

FILES = ["test003.sjava",
            "test007.sjava",
            "test011.sjava",
            "test017.sjava",
            "test052.sjava",
            "test058.sjava",
            "test061.sjava",
            "test067.sjava",
            "test106.sjava",
            "test115.sjava",
            "test204.sjava",
            "test257.sjava",
            "test261.sjava",
            "test274.sjava",
            "test407.sjava",
            "test420.sjava",
            "test427.sjava",
            "test452.sjava",
            "test474.sjava",
            "test501.sjava"]

tests = []
for filename in FILES:
    input_filename = filename
    expected_filename = filename
    tests.append({"name": "OutputTest " + filename,
                  "description": "A simple output test (file " + filename + ")",
                  "path": "OutputTest",
                  "args": [input_filename, expected_filename],
                  "timeout": 10,
                  "timeout_errorcodes": ["AUTO.presubmit_test_failed {" + filename + "}"]})
    
if __name__ == '__main__':
    properties = {
        "EX_NUM": "5",
        "EX_NAME": "Ex5",
        "EXPLANATION": "These tests verify your output against the expected results, in the given tests.",
        "TEST_TYPE": "presubmit",
        "MAX_SUBMISSION_GROUP_SIZE": 2, # submission in pairs, or singles
        "OTHER_FILES_ALLOWED": True,
        "EXTRACT_TO_SUBDIRECTORY": SOURCE_DIR_NAME,
        "SUPPLIED_FILES_DIRECTORY_PATH": os.path.join(EX_ROOT_PATH, "ex5/supplied"),
        "SUPPLIED_FILES": [],
        "COPY_DIRECOTRIES": [],
        "TESTS_DIRECTORY_PATH": os.path.join(EX_ROOT_PATH, "ex5/tests/presubmit/"),
        "TEST_FILES": ["OutputTest.java"],
        "TESTS": tests,
        "REQUIRED_FILES": ["ex5/main/Sjavac.java"],
        "SHOULD_NOT_SUBMIT": [],
        "OPTIONAL_FILES": ["META-INF", "META-INF/MANIFEST.MF"],
        "LINES_LENGTH_CHECK": True,
        "IGNORE_LINES_LENGTH_CHECK": [],
        "CHECK_DOCUMENTATION": True,
	"IGNORE_DOCUMENTATION_CHECK": [r".*Constants.*"]
    }
    runAll(properties)
