"""
 Copyright IBM Corp. 2020
"""

import csv
import filecmp
import hashlib
import logging
import os
import pytest
import shutil
import json


logging.basicConfig(format='%(asctime)s %(levelname)s %(message)s')
LOGGER = logging.getLogger(__name__)

DOCKER_IMAGE = os.getenv("IMAGE_NAME")


def _docker_run(current_test, input_folder="input", output_folder="output", config_folder="config", consistency_folder=None):
    """
    Executes the test by running the dpt docker image.

    @param current_test: path of the current test,
        where output/ and expected/ sub-folders are expected.
        If output is not existing will be created

    """
    current_test_folder = os.path.dirname(current_test)
    output = os.path.join(current_test_folder, output_folder)

    if not os.path.exists(output):
        os.makedirs(output)

    consistency = os.path.join(current_test_folder, consistency_folder) if consistency_folder else None

    if consistency and not os.path.exists(consistency):
        os.makedirs(consistency)

    os.system(
        "docker run --rm " \
        " --mount type=bind,source=" + os.path.join(current_test_folder, input_folder) + ",target=/input" \
        " --mount type=bind,source=" + os.path.join(current_test_folder, config_folder) + ",target=/config" \
        " --mount type=bind,source=" + output + ",target=/output " \
        " " + ("--mount type=bind,source=" + consistency +",target=/consistency" if consistency_folder else "") + "" \
        " " + DOCKER_IMAGE
    )


def _compare_dirs(dir1, dir2, comparer):
    """
    Compare two directories recursively. Files in each directory are
    assumed to be equal if their names and contents are equal.

    @param dir1: First directory path
    @param dir2: Second directory path

    @return: True if the directory trees are the same and
        there were no errors while accessing the directories or files,
        False otherwise.
    """
    result = True

    dirs_cmp = filecmp.dircmp(dir1, dir2)

    if len(dirs_cmp.left_only):
        for name in dirs_cmp.left_only:
            LOGGER.error("Unexpected file %s found in %s", name, dirs_cmp.left)
        result = False

    if len(dirs_cmp.right_only):
        for name in dirs_cmp.right_only:
            LOGGER.error("Expected file %s missing from %s", name, dirs_cmp.left)
        result = False

    if len(dirs_cmp.funny_files):
        for name in dirs_cmp.funny_files:
            LOGGER.error("Unexpected funny file %s found in %s", name, dirs_cmp.left)
        result = False

    for file in dirs_cmp.common_files:
        dir1_file = os.path.join(dir1, file)
        dir2_file = os.path.join(dir2, file)

        if not comparer(dir1_file, dir2_file):
            result = False

    for common_dir in dirs_cmp.common_dirs:
        new_dir1 = os.path.join(dir1, common_dir)
        new_dir2 = os.path.join(dir2, common_dir)
        if not _compare_dirs(new_dir1, new_dir2):
            result = False

    return result



def _compare_filename_only(current_test):
    current_test_folder = os.path.dirname(current_test)
    output_subfolder = os.path.join(current_test_folder, "output/")
    expected_subfolder = os.path.join(current_test_folder, "expected/")

    def comparer(dir1_file, dir2_file):
        return True

    assert compare_dirs(output_subfolder, expected_subfolder, comparer)


def _compare_entire_content(current_test, output="output/", expected="expected/", logging_level=None):
    if logging_level:
        old_logging_level = LOGGER.level
        LOGGER.setLevel(logging_level)

    current_test_folder = os.path.dirname(current_test)
    output_subfolder = os.path.join(current_test_folder, output)
    expected_subfolder = os.path.join(current_test_folder, expected)

    def comparer(dir1_file, dir2_file):
        BUF_SIZE = 65536

        dir1_file_sha256 = hashlib.sha256()
        dir2_file_sha256 = hashlib.sha256()

        with open(dir1_file, 'rb') as csv_dir1_file, open(dir2_file, 'rb') as csv_dir2_file:
            while True:
                data = csv_dir1_file.read(BUF_SIZE)
                LOGGER.debug("{}:\n{}".format(dir1_file, data))
                if not data:
                    break
                dir1_file_sha256.update(data)
            while True:
                data = csv_dir2_file.read(BUF_SIZE)
                LOGGER.debug("{}:\n{}".format(dir2_file, data))
                if not data:
                    break
                dir2_file_sha256.update(data)

        if dir1_file_sha256.digest() != dir2_file_sha256.digest():
            LOGGER.error("{} SHA256: {}".format(dir1_file, dir1_file_sha256.hexdigest()))
            LOGGER.error("{} SHA256: {}".format(dir2_file, dir2_file_sha256.hexdigest()))

            LOGGER.error("Content of %s does not match the one in %s", dir1_file, dir2_file)

            return False

        return True

    assert _compare_dirs(output_subfolder, expected_subfolder, comparer)

    if logging_level:
        LOGGER.setLevel(old_logging_level)


def _compare_json_content(current_test, output="output/", expected="expected/", logging_level=None):
    if logging_level:
        old_logging_level = LOGGER.level
        LOGGER.setLevel(logging_level)

    current_test_folder = os.path.dirname(current_test)
    output_subfolder = os.path.join(current_test_folder, output)
    expected_subfolder = os.path.join(current_test_folder, expected)

    def comparer(dir1_file, dir2_file):
        with open(dir1_file) as json_dir1_file, open(dir2_file) as json_dir2_file:
            json_dir1 = json.load(json_dir1_file)
            json_dir2 = json.load(json_dir2_file)

        if json_dir1 != json_dir2:
            LOGGER.error("Content of %s does not match the one in %s", dir1_file, dir2_file)

            return False

        return True

    assert _compare_dirs(output_subfolder, expected_subfolder, comparer)

    if logging_level:
        LOGGER.setLevel(old_logging_level)


def _compare_csv_total_rows_and_cols(current_test):
    current_test_folder = os.path.dirname(current_test)
    output_subfolder = os.path.join(current_test_folder, "output/")
    expected_subfolder = os.path.join(current_test_folder, "expected/")

    def comparer(dir1_file, dir2_file):
        with open(dir1_file) as csv_dir1_file, open(dir2_file) as csv_dir2_file:
            csv_dir1_reader = csv.reader(csv_dir1_file, delimiter=',')
            csv_dir2_reader = csv.reader(csv_dir2_file, delimiter=',')

            csv_dir1_file.seek(0)
            dir1_rows = sum(1 for row in csv_dir1_reader)

            csv_dir2_file.seek(0)
            dir2_rows = sum(1 for row in csv_dir2_reader)

            if dir1_rows != dir2_rows:
                LOGGER.error("Number of rows of %s does not match the one in %s", dir1_file, dir2_file)

            csv_dir1_file.seek(0)
            dir1_columns = len(next(csv_dir1_reader))

            csv_dir2_file.seek(0)
            dir2_columns = len(next(csv_dir2_reader))

            if dir1_columns != dir2_columns:
                LOGGER.error("Number of columns of %s does not match the one in %s", dir1_file, dir2_file)

        return True

    assert _compare_dirs(output_subfolder, expected_subfolder, comparer)


@pytest.fixture(scope="module", autouse=True)
def clean_output(request, folder="output"):
    module_folder = os.path.dirname(os.path.realpath(request.module.__file__))
    output_folder = os.path.join(module_folder, folder)

    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
        return

    for filename in os.listdir(output_folder):
        file_path = os.path.join(output_folder, filename)
        try:
                if os.path.isfile(file_path) or os.path.islink(file_path):
                    os.unlink(file_path)
                elif os.path.isdir(file_path):
                    shutil.rmtree(file_path)
                else:
                    print("Weird: %s is not a file nor a directory?".format(file_path))
        except Exception as e:
            print('Failed to delete %s.'.format(file_path))
            print(e)


@pytest.fixture
def docker_run():
    """Helper method for injecting the docker_run method."""
    return _docker_run


@pytest.fixture
def compare_entire_content():
    return _compare_entire_content


@pytest.fixture
def compare_csv_total_rows_and_cols():
    return _compare_csv_total_rows_and_cols


@pytest.fixture
def compare_filename_only():
    return _compare_filename_only


@pytest.fixture
def compare_json_content():
    return _compare_json_content
