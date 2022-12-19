"""
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/
"""


def test_validate(docker_run, compare_entire_content):
    """
    The test.
    """
    docker_run(__file__)

    compare_entire_content(__file__)
