package com.rootcode.skapp.peopleplanner.service;

import com.rootcode.skapp.common.payload.response.ResponseEntityDto;
import com.rootcode.skapp.peopleplanner.payload.request.TeamPatchRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.TeamRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.TeamsRequestDto;
import com.rootcode.skapp.peopleplanner.payload.request.TransferTeamMembersDto;

import java.util.List;

public interface TeamService {

	ResponseEntityDto addNewTeam(TeamRequestDto teamRequestDto);

	ResponseEntityDto getAllTeamDetails();

	ResponseEntityDto updateTeam(Long id, TeamPatchRequestDto teamPatchRequestDto);

	ResponseEntityDto getTeamByTeamId(Long id);

	ResponseEntityDto addTeams(TeamsRequestDto teamsRequestDto);

	ResponseEntityDto transferMembersAndDeleteTeam(Long id, List<TransferTeamMembersDto> transferTeamMembersDtoList);

	ResponseEntityDto getManagerTeams();

	ResponseEntityDto getTeamsForCurrentUser();

}
