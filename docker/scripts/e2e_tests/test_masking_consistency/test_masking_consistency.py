"""
Copyright IBM Corp. 2020
"""

def test_validate(docker_run, compare_entire_content, ):
    """
    The test.
    """
    docker_run(__file__, output_folder='output1', consistency_folder='consistency')
    docker_run(__file__, output_folder='output2', consistency_folder='consistency')

    compare_entire_content(__file__, output='output1', expected='output2')
