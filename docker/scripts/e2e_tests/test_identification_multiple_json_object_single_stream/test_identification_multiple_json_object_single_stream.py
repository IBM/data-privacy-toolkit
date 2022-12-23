"""
 Copyright IBM Corp. 2020
"""


def test_validate_multiple_json_documents_in_single_file_are_processed(docker_run, compare_entire_content):
    """
    The test.
    """
    docker_run(__file__)

    compare_entire_content(__file__)
